package com.chillminds.local_construction.repositories.remote.service

import com.chillminds.local_construction.repositories.remote.dto.*
import retrofit2.http.*

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

    @Headers(contentType)
    @POST("/v1/project/update")
    suspend fun updateProject(
        @Header("Authorization") auth: String,
        @Body request: ProjectDetail,
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/project/create")
    suspend fun createProject(
        @Header("Authorization") auth: String,
        @Body request: ProjectCreationRequest
    ): CommonResponse

    @Headers(contentType)
    @GET("/v1/materials/{id}")
    suspend fun getAllMaterials(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String = "all",
    ): MaterialListResponse

    @Headers(contentType)
    @GET("/v1/labour/{id}")
    suspend fun getAllLabourInfo(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String = "all",
    ): LabourInfoResponse

    @Headers(contentType)
    @GET("/v1/stages/{id}")
    suspend fun getAllStagesInfo(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String = "all",
    ): StagesInfoResponse

    @Headers(contentType)
    @POST("/v1/materials/update")
    suspend fun updateMaterials(
        @Header("Authorization") auth: String,
        @Body request: MaterialUpdateRequest,
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/labour/update")
    suspend fun updateLabourDetails(
        @Header("Authorization") auth: String,
        @Body request: LabourUpdateRequest,
    ): CommonResponse

}
