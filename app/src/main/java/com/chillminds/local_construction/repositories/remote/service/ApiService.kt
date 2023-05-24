package com.chillminds.local_construction.repositories.remote.service

import com.chillminds.local_construction.repositories.remote.dto.ProjectDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiService {

    companion object {
        private const val contentType = "Content-Type: application/json"
        private const val noCache = "Cache-Control:no-store, no-cache"
    }

    @Headers(contentType)
    @GET("/v1/project/{id}")
    suspend fun getAllProjects(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String = "all",
        ): ProjectDetailsResponse

}
