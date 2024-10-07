package com.chillminds.local_construction.repositories.remote

import com.chillminds.local_construction.repositories.remote.dto.LabourData
import com.chillminds.local_construction.repositories.remote.dto.MaterialData
import com.chillminds.local_construction.repositories.remote.dto.ProjectCreationRequest
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.repositories.remote.dto.RentalInformation
import com.chillminds.local_construction.repositories.remote.dto.RentalProduct
import com.chillminds.local_construction.repositories.remote.dto.RentalProductCreationRequest
import com.chillminds.local_construction.repositories.remote.service.ApiHelper

class RemoteRepository(
    private val remoteRepo: ApiHelper
) {
    suspend fun getProject(id: String? = null) = id?.let {
        remoteRepo.getAllProjectById(it)
    } ?: run {
        remoteRepo.getAllProjects()
    }
    suspend fun getRentalProductDetails() = remoteRepo.getRentalProductDetails()
    suspend fun getRentalDataList() = remoteRepo.getRentalDataList()

    suspend fun createNewProject(request: ProjectCreationRequest) = remoteRepo.createProject(request)
    suspend fun createRentalProduct(request: RentalProductCreationRequest) = remoteRepo.createRentalProduct(request)
    suspend fun createRentalEntry(request: RentalInformation) = remoteRepo.createRentalEntry(request)

    suspend fun updateProject(request: ProjectDetail) = remoteRepo.updateExistingProject(request)
    suspend fun updateRentalProduct(request: RentalProduct) = remoteRepo.updateRentalProduct(request)
    suspend fun updateRentalData(request: RentalInformation) = remoteRepo.updateRentalData(request)

    suspend fun deleteProject(request: ProjectDetail) = remoteRepo.removeProject(request.id)
    suspend fun deleteRentalProduct(request: RentalProduct) = remoteRepo.removeRentalProduct(request.id)
    suspend fun deleteRentalEntry(request: RentalInformation) = remoteRepo.removeRentalEntry(request.id?:"")

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

    suspend fun getStagesInformation(id: String? = null) = id?.let {
        remoteRepo.getStagesInfoById(it)
    } ?: run {
        remoteRepo.getAllStagesInfo()
    }

    suspend fun updateMaterial(data: MaterialData) = remoteRepo.updateMaterials(arrayListOf(data))

    suspend fun updateLabour(data: LabourData) = remoteRepo.updateLabourDetails(arrayListOf(data))

}
