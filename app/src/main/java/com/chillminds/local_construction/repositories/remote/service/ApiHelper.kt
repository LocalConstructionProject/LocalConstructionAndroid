package com.chillminds.local_construction.repositories.remote.service

import com.chillminds.local_construction.repositories.remote.dto.*

class ApiHelper(
    private val service: ApiService,
    private val authentication: String
) {

    suspend fun getAllProjects() = service.getAllProjects(authentication)
    suspend fun getRentalProductDetails() = service.getRentalProductDetails(authentication)
    suspend fun getRentalDataList() = service.getRentalDataList(authentication)

    suspend fun getAllProjectById(id: String) = service.getAllProjects(authentication, id)

    suspend fun removeProject(id: String) = service.removeProject(authentication, id)
    suspend fun removeRentalProduct(id: String) = service.removeRentalProduct(authentication, id)
    suspend fun removeRentalEntry(id: String) = service.removeRentalEntry(authentication, id)

    suspend fun createProject(request: ProjectCreationRequest) =
        service.createProject(authentication, request)

    suspend fun createRentalProduct(request: RentalProductCreationRequest) =
        service.createRentalProduct(authentication, request)

    suspend fun createRentalEntry(request: RentalInformation) =
        service.createRentalEntry(authentication, request)

    suspend fun updateExistingProject(request: ProjectDetail) =
        service.updateProject(authentication, request)

    suspend fun updateRentalProduct(request: RentalProduct) =
        service.updateRentalProduct(authentication, request, request.id)

    suspend fun updateRentalData(request: RentalInformation) =
        service.updateRentalData(authentication, request, request.id ?: "")

    suspend fun getMaterialById(id: String) = service.getAllMaterials(authentication, id)

    suspend fun getAllMaterials() = service.getAllMaterials(authentication)

    suspend fun getLabourInfoById(id: String) = service.getAllLabourInfo(authentication, id)

    suspend fun getAllLabourInfo() = service.getAllLabourInfo(authentication)

    suspend fun getStagesInfoById(id: String) = service.getAllStagesInfo(authentication, id)

    suspend fun getAllStagesInfo() = service.getAllStagesInfo(authentication)

    suspend fun updateMaterials(request: List<MaterialData>) =
        service.updateMaterials(authentication, MaterialUpdateRequest(request))

    suspend fun updateLabourDetails(request: List<LabourData>) =
        service.updateLabourDetails(authentication, LabourUpdateRequest(request))

}