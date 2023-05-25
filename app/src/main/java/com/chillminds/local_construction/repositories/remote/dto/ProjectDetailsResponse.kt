package com.chillminds.local_construction.repositories.remote.dto

import com.chillminds.local_construction.utils.getDateTime
import com.google.gson.annotations.SerializedName

data class ProjectDetailsResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<ProjectDetail>,
)

data class MaterialListResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<MaterialData>,
)

data class MaterialData(
    val _id: String,
    val name: String,
    @SerializedName("amount") val price: Int,
    val id: String,
)

data class LabourInfoResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<LabourData>,
)

data class LabourData(
    val _id: String,
    val name: String,
    val price: Int,
    val id: String,
)

data class ProjectDetail(
    @SerializedName("_id") val id: String,
    val name: String,
    val createdDate: String = getDateTime(),
    val location: String,
    val contact: String?,
    val stages: List<StageDetail> = arrayListOf(),
)

data class StageDetail(
    val _id: String,
    val id: String,
    val name: String,
    val createdDate: String = getDateTime(),
    val labourDetails: List<LabourDetail> = arrayListOf(),
    val materialDetails: List<MaterialDetail> = arrayListOf(),
)

data class LabourDetail(
    val _id: String,
    val name: String,
    val price: Int,
    val date: String = getDateTime(),
    val labourCount: Int,
    val id: String,
)

data class MaterialDetail(
    val _id: String,
    val name: String,
    val price: Int,
    val date: String = getDateTime(),
    val materialCount: Int,
    val id: String,
)