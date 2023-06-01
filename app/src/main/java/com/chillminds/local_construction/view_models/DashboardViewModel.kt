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

class DashboardViewModel(
    application: Application,
    val commonModel: CommonModel,
    private val repository: RemoteRepository,
) : AndroidViewModel(application) {

    val materialDataToEdit = MutableLiveData<MaterialData?>().apply { value = null }
    val labourDataToEdit = MutableLiveData<LabourData?>().apply { value = null }
    val stageEntryDataToEdit =
        MutableLiveData<Pair<StageEntryRecord?, ProjectStageDetail>>().apply { value = null }
    val projectDataToEdit = MutableLiveData<ProjectDetail?>().apply { value = null }

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

    fun editStageEntryData(data: StageEntryRecord, stageDetails: ProjectStageDetail) {
        stageEntryDataToEdit.postValue(Pair(data, stageDetails))
        commonModel.actionListener.postValue(Actions.SHOW_STAGE_ENTRY_EDIT_DIALOG)
    }

    fun editProjectData(data: ProjectDetail) {
        projectDataToEdit.postValue(data)
        commonModel.actionListener.postValue(Actions.SHOW_PROJECT_EDIT_DIALOG)
    }

    fun addStageBottomSheet() {
        commonModel.actionListener.postValue(Actions.SHOW_CREATE_STAGE_DIALOG)
    }

    fun createProject(name: String, location: String, contact: Long) = liveData {
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    repository.createNewProject(
                        ProjectCreationRequest(
                            name = name,
                            location = location,
                            contact = contact
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
    }

}