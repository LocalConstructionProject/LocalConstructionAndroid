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

    val materialDataToEdit = MutableLiveData<MaterialData?>().apply { value = null }
    val labourDataToEdit = MutableLiveData<LabourData?>().apply { value = null }
    val stageEntryDataToEdit =
        MutableLiveData<Pair<StageEntryRecord?, ProjectStageDetail>>().apply { value = null }
    val projectStagesTabAdapterPosition =
        MutableLiveData<ProjectStageDetail?>().apply { value = null }
    val spinnerSelectedPosition = MutableLiveData<Int>().apply { value = 0 }
    val projectStagesTabPosition = MutableLiveData<Int>().apply { value = -1 }
    val projectDashboardPosition = MutableLiveData<Int>().apply { value = -1 }

    val materialEntryRecord = MutableLiveData<StageEntryRecord?>()
    val newMaterialEntrySpinnerSelection = MutableLiveData<StageEntryRecord?>()
    val count = MutableLiveData<String>().apply { value = "1" }
    val price = MutableLiveData<String>().apply { value = "1" }
    val date = MutableLiveData<String>().apply { value = getDateTime() }

    fun onSelectDateToEntry() {
        commonModel.actionListener.postValue(Actions.SHOW_DATE_BOTTOM_SHEET)
    }

    fun exportPdfFromProjectDashboard() {
        if (isSdkHigherThan28()) {
            commonModel.actionListener.postValue(Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS)
        } else {
            commonModel.actionListener.postValue(Actions.CHECK_PERMISSION_FOR_STORAGE)
        }
    }

    fun showStageEntryChildOptionsDeleteSheet(data: StageEntryRecord, stageDetails: ProjectStageDetail) {
        projectStagesTabAdapterPosition.postValue(stageDetails)
        stageEntryDataToEdit.postValue(Pair(data, stageDetails))
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

    fun deleteProjectData(data: ProjectDetail) {
        commonModel.projectToDelete.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_PROJECT_DELETION_CONFIRMATION_DIALOG)
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

    fun insertOrUpdateEntry() {
        commonModel.actionListener.postValue(Actions.INSERT_OR_VALIDATE_ENTRY)
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

}