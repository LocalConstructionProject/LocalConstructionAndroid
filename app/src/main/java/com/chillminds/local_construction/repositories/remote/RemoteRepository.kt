package com.chillminds.local_construction.repositories.remote

import com.chillminds.local_construction.repositories.remote.service.ApiHelper

class RemoteRepository(
    private val remoteRepo: ApiHelper
) {
    suspend fun getProject(id: String? = null) = id?.let {
        remoteRepo.getAllProjectById(it)
    } ?: run {
        remoteRepo.getAllProjects()
    }

    suspend fun getMaterial(id: String? = null) = id?.let {
        remoteRepo.getMaterialById(it)
    } ?: run {
        remoteRepo.getAllMaterials()
    }

    suspend fun getLabourInfo(id: String? = null) = id?.let {
        remoteRepo.getLabourInfoById(it)
    } ?: run {
        remoteRepo.getAllLabourInfo()
    }
}
