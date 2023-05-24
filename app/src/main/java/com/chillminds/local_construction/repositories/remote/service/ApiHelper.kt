package com.chillminds.local_construction.repositories.remote.service

class ApiHelper(
    private val service: ApiService,
    private val authentication: String
) {

    suspend fun getAllProjects() = service.getAllProjects(authentication)

    suspend fun getAllProjectById(id: String) = service.getAllProjects(authentication, id)

}