package com.chillminds.local_construction.repositories.remote.service

class ApiHelper(
    private val service: ApiService,
    private val authentication: String
) {

    suspend fun getAllProjects() = service.getAllProjects(authentication)

    suspend fun getAllProjectById(id: String) = service.getAllProjects(authentication, id)

    suspend fun getMaterialById(id: String) = service.getAllMaterials(authentication, id)

    suspend fun getAllMaterials() = service.getAllMaterials(authentication)

    suspend fun getLabourInfoById(id: String) = service.getAllLabourInfo(authentication, id)

    suspend fun getAllLabourInfo() = service.getAllLabourInfo(authentication)
}