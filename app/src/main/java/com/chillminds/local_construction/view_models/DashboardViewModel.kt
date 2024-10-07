package com.chillminds.local_construction.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.models.CommonModel
import com.chillminds.local_construction.repositories.remote.RemoteRepository
import com.chillminds.local_construction.repositories.remote.Resource
import com.chillminds.local_construction.repositories.remote.dto.*
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.getDateTime
import com.chillminds.local_construction.utils.isSdkHigherThan28

class DashboardViewModel(
    application: Application,
    val commonModel: CommonModel,
    private val repository: RemoteRepository,
) : AndroidViewModel(application) {

    var rentalInformation: RentalInformation? = null
    var returnStatus = "Returned"

    val materialDataToEdit = MutableLiveData<MaterialData?>().apply { value = null }
    val labourDataToEdit = MutableLiveData<LabourData?>().apply { value = null }
    val stageEntryDataToEdit =
        MutableLiveData<Pair<StageEntryRecord?, ProjectStageDetail>>().apply { value = null }
    val projectStagesTabAdapterPosition =
        MutableLiveData<ProjectStageDetail?>().apply { value = null }
    val spinnerSelectedPosition = MutableLiveData<Int>().apply { value = 0 }
    val projectStagesTabPosition = MutableLiveData<Int>().apply { value = -1 }
    val projectDashboardPosition = MutableLiveData<Int>().apply { value = -1 }
    val expandedDashboardSpinnerPosition = MutableLiveData<Int>().apply { value = -1 }

    val materialEntryRecord = MutableLiveData<StageEntryRecord?>()
    val projectPaymentDetailToUpdate = MutableLiveData<ProjectPaymentDetail?>()
    val selectedDashboardStatistics = MutableLiveData<DashboardStatisticsDetails?>()
    val listInfo = MutableLiveData<ListInfo?>()
    val projectPaymentDetailsToShow = MutableLiveData<ArrayList<ProjectPaymentDetail>?>()
    val newMaterialEntrySpinnerSelection = MutableLiveData<StageEntryRecord?>()
    val count = MutableLiveData<String>().apply { value = "1" }
    val price = MutableLiveData<String>().apply { value = "1" }
    val paymentType = MutableLiveData<PaymentType>().apply { value = PaymentType.CASH }
    val paymentValue = MutableLiveData<String>().apply { value = "1" }
    val date = MutableLiveData<String>().apply { value = getDateTime() }
    val paymentDate = MutableLiveData<String>().apply { value = getDateTime() }

    fun onSelectDateToEntry() {
        commonModel.actionListener.postValue(Actions.SHOW_DATE_BOTTOM_SHEET)
    }

    fun onSelectDateForPayment() {
        commonModel.actionListener.postValue(Actions.SHOW_DATE_BOTTOM_SHEET_FOR_PAYMENT_DATE)
    }

    fun showDialogToRentalReport() {
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_REPORT_CREATION_PAGE)
    }

    fun createNewProductForRental() {
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_PRODUCT_CREATION_PAGE)
    }

    fun exportPdfFromProjectDashboard() {
        if (isSdkHigherThan28()) {
            commonModel.actionListener.postValue(Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS)
        } else {
            commonModel.actionListener.postValue(Actions.CHECK_PERMISSION_FOR_STORAGE)
        }
    }

    fun showStageWiseInfo(data: ProjectStageDetail) {
        data.entryRecords.let { entryList ->
            val count = entryList.sumOf { it.count }.toString()
            val totalPrice = entryList.sumOf { it.totalPrice }.toString()
            val labourCount =
                entryList.filter { it.type == StageEntryType.LABOUR }.sumOf { it.count }.toString()
            val materialCount =
                entryList.filter { it.type == StageEntryType.MATERIAL }.sumOf { it.count }
                    .toString()
            val others = entryList.groupBy { it.name }.map {
                Pair(
                    it.key,
                    "${it.value.sumOf { it.count }} ( Rs. ${it.value.sumOf { it.totalPrice }} )"
                )
            }
            listInfo.postValue(ListInfo(count, totalPrice, labourCount, materialCount, others))
        }
        commonModel.actionListener.postValue(Actions.SHOW_LIST_INFO_DIALOG)
    }

    fun showInformationForProjectWiseRecord() {
        val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()

        commonModel.dashboardProjectDetail.value?.stages?.forEach { stageDetails ->
            val entries = stageDetails.entryRecords.filter {
                when (projectDashboardPosition.value) {
                    0 -> it.type == StageEntryType.LABOUR || it.type == StageEntryType.MATERIAL
                    1 -> it.type == StageEntryType.MATERIAL
                    else -> it.type == StageEntryType.LABOUR
                }
            }
            dashboardEntryDetails.addAll(
                entries.map {
                    it.toDashboardStatisticsDetails(
                        commonModel.dashboardProjectDetail.value!!,
                        stageDetails,
                        entries
                    )
                })
        }

        val listToDisplay =
            dashboardEntryDetails.groupBy { it.entryName }.filter { it.value.isNotEmpty() }.map {
                val entryName = it.key
                val entries = it.value
                entries.first().let { data ->
                    DashboardStatisticsDetails(
                        data.projectId,
                        data.projectName,
                        data.stageId,
                        data.stageName,
                        data.stageEntry,
                        entryName,
                        entries.map { it.stageEntry },
                        entries.sumOf { it.count },
                        entries.sumOf { it.totalPrice })
                }
            }

        val count = listToDisplay.sumOf { it.count }.toString()
        val totalPrice = listToDisplay.sumOf { it.totalPrice }.toString()
        val labourCount =
            listToDisplay.filter { it.stageEntry.type == StageEntryType.LABOUR }.sumOf { it.count }
                .toString()
        val materialCount =
            listToDisplay.filter { it.stageEntry.type == StageEntryType.MATERIAL }
                .sumOf { it.count }
                .toString()
        val others = listToDisplay.groupBy { it.entryName }.map {
            Pair(
                it.key,
                "${it.value.sumOf { it.count }} ( Rs. ${it.value.sumOf { it.totalPrice }} )"
            )
        }
        listInfo.postValue(ListInfo(count, totalPrice, labourCount, materialCount, others))

        commonModel.actionListener.postValue(Actions.SHOW_LIST_INFO_DIALOG)

    }

    fun showInformation(
        projectList: List<ProjectDetail>?,
        sortByPosition: Int
    ) {
        val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()
        projectList?.forEach { projectDetail ->
            projectDetail.stages.forEach { stageDetails ->
                dashboardEntryDetails.addAll(stageDetails.entryRecords.map {
                    it.toDashboardStatisticsDetails(
                        projectDetail,
                        stageDetails,
                        stageDetails.entryRecords
                    )
                })
            }
        }

        val listToDisplay =
            dashboardEntryDetails.groupBy { it.entryName }.filter { it.value.isNotEmpty() }.map {
                val entryName = it.key
                val entries = it.value
                entries.first().let { data ->
                    DashboardStatisticsDetails(
                        data.projectId,
                        data.projectName,
                        data.stageId,
                        data.stageName,
                        data.stageEntry,
                        entryName,
                        entries.map { it.stageEntry },
                        entries.sumOf { it.count },
                        entries.sumOf { it.totalPrice })
                }
            }

        val finalData = when (sortByPosition) {
            1 -> listToDisplay.sortedBy { it.count }
            2 -> listToDisplay.sortedBy { it.totalPrice }
            3 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.LABOUR }
                .sortedBy { it.stageId }

            4 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.MATERIAL }
                .sortedBy { it.stageId }

            else -> listToDisplay
        }
        val count = finalData.sumOf { it.count }.toString()
        val totalPrice = finalData.sumOf { it.totalPrice }.toString()
        val labourCount =
            finalData.filter { it.stageEntry.type == StageEntryType.LABOUR }.sumOf { it.count }
                .toString()
        val materialCount =
            finalData.filter { it.stageEntry.type == StageEntryType.MATERIAL }.sumOf { it.count }
                .toString()
        val others = finalData.groupBy { it.entryName }.map {
            Pair(
                it.key,
                "${it.value.sumOf { it.count }} ( Rs. ${it.value.sumOf { it.totalPrice }} )"
            )
        }
        listInfo.postValue(ListInfo(count, totalPrice, labourCount, materialCount, others))
        commonModel.actionListener.postValue(Actions.SHOW_LIST_INFO_DIALOG)
    }

    fun showStageEntryChildOptionsDeleteSheet(
        data: StageEntryRecord,
        stageDetails: ProjectStageDetail
    ) {
        projectStagesTabAdapterPosition.postValue(stageDetails)
        stageEntryDataToEdit.postValue(Pair(data, stageDetails))
        commonModel.actionListener.postValue(Actions.SHOW_STAGE_ENTRY_OPTIONS_DIALOG)
    }

    fun updateEntryInformation(
        stageEntry: StageEntryRecord? = null,
    ) {
        materialEntryRecord.postValue(stageEntry)
        count.postValue((stageEntry?.count ?: 1).toString())
        price.postValue((stageEntry?.priceForTheDay ?: 1).toString())
        date.postValue(
            stageEntry?.dateOfExecution?.dateConversion() ?: getDateTime().dateConversion()
        )
    }

    fun showBottomSheetToCreateProject() {
        commonModel.actionListener.postValue(Actions.SHOW_PROJECT_CREATION_SHEET)
    }

    fun editMaterial(data: MaterialData) {
        materialDataToEdit.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_MATERIAL_EDIT_DIALOG)
    }

    fun editLabourInfo(data: LabourData) {
        labourDataToEdit.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_LABOUR_EDIT_DIALOG)
    }

    fun editProjectData(data: ProjectDetail) {
        commonModel.selectedProjectDetail.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_PROJECT_EDIT_DIALOG)
    }

    fun editRentalProductData(data: RentalProduct) {
        commonModel.selectedRentalProduct.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_DATA_EDIT_DIALOG)
    }

    fun editRentalInformation(data: RentalInformation) {
        commonModel.selectedRentalInformation.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_INFORMATION_EDIT_DIALOG)
    }

    fun showRentalInfoDialog(data: RentalInformation) {
        commonModel.selectedRentalInformation.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_RENT_INFORMATION_DIALOG)
    }

    fun deleteProjectData(data: ProjectDetail) {
        commonModel.projectToDelete.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_PROJECT_DELETION_CONFIRMATION_DIALOG)
    }

    fun deleteRentalProductData(data: RentalProduct) {
        commonModel.rentalProductToDelete.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_PRODUCT_DELETION_CONFIRMATION_DIALOG)
    }

    fun deleteRentalProductData(data: RentalInformation) {
        commonModel.rentalInfoToDelete.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_RENTAL_INFORMATION_DELETION_CONFIRMATION_DIALOG)
    }

    fun showInfo(data: ProjectDetail) {
        data.stages.flatMap { it.entryRecords }.let { entryList ->
            val count = entryList.sumOf { it.count }.toString()
            val totalPrice = entryList.sumOf { it.totalPrice }.toString()
            val labourCount =
                entryList.filter { it.type == StageEntryType.LABOUR }.sumOf { it.count }.toString()
            val materialCount =
                entryList.filter { it.type == StageEntryType.MATERIAL }.sumOf { it.count }
                    .toString()
            val others = entryList.groupBy { it.name }.map {
                Pair(
                    it.key,
                    "${it.value.sumOf { it.count }} ( Rs. ${it.value.sumOf { it.totalPrice }} )"
                )
            }
            listInfo.postValue(ListInfo(count, totalPrice, labourCount, materialCount, others))
        }
        commonModel.actionListener.postValue(Actions.SHOW_LIST_INFO_DIALOG)
    }

    fun showPaymentInfo(data: ProjectDetail) {
        projectPaymentDetailsToShow.postValue(ArrayList(data.paymentDetails))
        commonModel.actionListener.postValue(Actions.SHOW_LIST_PAYMENT_INFO_DIALOG)
    }

    fun onSelectSpecificProjectFromDashboard(data: ProjectDetail) {
        commonModel.dashboardProjectDetail.postValue(data)
        commonModel.actionListener.postValue(Actions.ON_SELECT_PROJECT_FROM_DASHBOARD)
    }

    fun addStageBottomSheet() {
        commonModel.actionListener.postValue(Actions.SHOW_SHEET_TO_CHOOSE_OPTION_ON_HOME)
    }

    fun dismissStageEntryBottomSheet() {
        commonModel.actionListener.postValue(Actions.DISMISS_STAGE_ENTRY_BOTTOM_SHEET)
    }

    fun dismissPaymentEntryBottomSheet() {
        commonModel.actionListener.postValue(Actions.DISMISS_PAYMENT_ENTRY_BOTTOM_SHEET)
    }

    fun insertOrUpdateEntry() {
        commonModel.actionListener.postValue(Actions.INSERT_OR_VALIDATE_ENTRY)
    }

    fun insertOrUpdatePaymentEntry() {
        commonModel.actionListener.postValue(Actions.INSERT_OR_VALIDATE_PAYMENT_ENTRY)
    }

    fun createRentalProduct(name: String, quantity: Int, rentalPrice: Int) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.createRentalProduct(
                        RentalProductCreationRequest(
                            name = name, quantity = quantity, rentalPrice = rentalPrice
                        )
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun createRentalEntry() = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    rentalInformation?.let { entry ->
                        repository.createRentalEntry(
                            entry
                        )
                    }
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateRentalEntry() = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    rentalInformation?.let { entry ->
                        repository.updateRentalData(
                            entry
                        )
                    }
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun createProject(name: String, location: String, contact: Long) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.createNewProject(
                        ProjectCreationRequest(
                            name = name, location = location, contact = contact
                        )
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateProject(project: ProjectDetail) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateProject(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateRentalProduct(project: RentalProduct) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateRentalProduct(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun deleteProject(project: ProjectDetail) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.deleteProject(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun deleteRentalProduct(project: RentalProduct) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.deleteRentalProduct(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun deleteRentalEntry(project: RentalInformation) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.deleteRentalEntry(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun createStage(project: ProjectDetail, stage: StageDetail) = liveData {
        val stages =
            project.stages + listOf(ProjectStageDetail(name = stage.name)).distinctBy { it.name }
        project.stages = stages
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateProject(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateStage(project: ProjectDetail, stage: ProjectStageDetail) = liveData {
        project.stages.forEach {
            if (it.id == stage.id) {
                it.name = stage.name
                it.id = stage.id
                it.startedDate = stage.startedDate
                it.entryRecords = stage.entryRecords
            }
        }
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateProject(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updatePayment(project: ProjectDetail, stage: ProjectPaymentDetail) = liveData {
        project.paymentDetails.forEach {
            if (it.id == stage.id) {
                it.paymentType = stage.paymentType
                it.dateOfPayment = stage.dateOfPayment
                it.payment = stage.payment
            }
        }
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateProject(
                        project
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateMaterial(materialData: MaterialData) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateMaterial(
                        materialData
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun updateLabour(labourData: LabourData) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.updateLabour(
                        labourData
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.error(null, "${e.message}"))
        }
    }

    fun getAllProjects() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getProject()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun getRentalProductDetails() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getRentalProductDetails()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun getRentalDataList() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getRentalDataList()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun getAllMaterials() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getMaterial()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun getLabourDetails() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getLabourInfo()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun getStagesData() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getStagesInformation()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

    fun selectProject(data: ProjectDetail) {
        commonModel.selectedProjectDetail.postValue(data)
        val selectedStage = projectStagesTabAdapterPosition.value
        val stageToUpdate = data.stages.firstOrNull { stage -> stage.name == selectedStage?.name }
            ?: data.stages.firstOrNull()
        projectStagesTabAdapterPosition.postValue(stageToUpdate)
    }

    fun showDashboardEntryExpansion(dashboardStatistics: DashboardStatisticsDetails) {
        selectedDashboardStatistics.postValue(dashboardStatistics)
        commonModel.actionListener.postValue(Actions.SHOW_DASHBOARD_STATISTICS_EXPANSION_DETAILS)
    }
}
