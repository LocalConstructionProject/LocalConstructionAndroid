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
    @GET("/v1/product")
    suspend fun getRentalProductDetails(
        @Header("Authorization") auth: String,
    ): RentalProductDetailsResponse

    @Headers(contentType)
    @GET("/v1/rentalData")
    suspend fun getRentalDataList(
        @Header("Authorization") auth: String,
    ): RentalDataListResponse

    @Headers(contentType)
    @GET("/v1/project/delete/{id}")
    suspend fun removeProject(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String = "all",
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/project/update")
    suspend fun updateProject(
        @Header("Authorization") auth: String,
        @Body request: ProjectDetail,
    ): CommonResponse

    @Headers(contentType)
    @PATCH("/v1/product/update/{id}")
    suspend fun updateRentalProduct(
        @Header("Authorization") auth: String,
        @Body request: UpdateRentalProduct,
        @Path("id", encoded = true) id: String,
    ): CommonResponse

    @Headers(contentType)
    @PATCH("/v1/rentalData/update/{id}")
    suspend fun updateRentalData(
        @Header("Authorization") auth: String,
        @Body request: UpdateRentalInformation,
        @Path("id", encoded = true) id: String,
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/project/create")
    suspend fun createProject(
        @Header("Authorization") auth: String,
        @Body request: ProjectCreationRequest
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/product/create")
    suspend fun createRentalProduct(
        @Header("Authorization") auth: String,
        @Body request: RentalProductCreationRequest
    ): CommonResponse

    @Headers(contentType)
    @POST("/v1/rentalData/create")
    suspend fun createRentalEntry(
        @Header("Authorization") auth: String,
        @Body request: UpdateRentalInformation
    ): CommonResponse

    @Headers(contentType)
    @DELETE("/v1/product/delete/{id}")
    suspend fun removeRentalProduct(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String,
    ): CommonResponse

    @Headers(contentType)
    @DELETE("/v1/rentalData/delete/{id}")
    suspend fun removeRentalEntry(
        @Header("Authorization") auth: String,
        @Path("id", encoded = true) id: String,
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
