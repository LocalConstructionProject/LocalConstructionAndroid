package com.chillminds.local_construction.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.chillminds.local_construction.models.CommonModel
import com.chillminds.local_construction.repositories.remote.RemoteRepository
import com.chillminds.local_construction.repositories.remote.Resource

class SplashViewModel(
    application: Application,
    val commonModel: CommonModel,
    private val repository: RemoteRepository,
) : AndroidViewModel(application) {

    val splashMessage = MutableLiveData<String>().apply { value = "Connecting with server..!" }

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

    fun getStagesData()= liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getStagesInformation()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.toString()))
        }
    }

}