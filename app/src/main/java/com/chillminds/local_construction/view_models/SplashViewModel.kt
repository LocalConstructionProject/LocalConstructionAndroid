package com.chillminds.local_construction.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.chillminds.local_construction.models.CommonModel
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.RemoteRepository
import com.chillminds.local_construction.repositories.remote.Resource
import com.chillminds.local_construction.repositories.remote.service.ApiHelper

class SplashViewModel(
    application: Application,
    val commonModel: CommonModel,
    val repository: RemoteRepository,
) : AndroidViewModel(application) {

    fun getAllProjects() = liveData {
        emit(Resource.loading())
        try {
            emit(Resource.success(repository.getProject()))
        } catch (e: Exception) {
            emit(Resource.error(null, ""))
        }
    }

}