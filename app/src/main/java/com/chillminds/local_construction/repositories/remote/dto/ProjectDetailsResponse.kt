package com.chillminds.local_construction.repositories.remote.dto

import com.chillminds.local_construction.utils.getDateTime
import com.google.gson.annotations.SerializedName

data class ProjectDetailsResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<ProjectDetail> = arrayListOf(),
)

data class CommonResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: Any,
)

data class MaterialListResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<MaterialData> = arrayListOf(),
)

data class MaterialData(
    val _id: String,
    var name: String,
    var amount: Int,
    val id: String,
)

data class LabourInfoResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<LabourData>,
)

data class StagesInfoResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<StageDetail> = arrayListOf(),
)

data class LabourData(
    val _id: String,
    var name: String,
    var price: Int,
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

data class ProjectCreationRequest(
    val name: String,
    val createdDate: String = getDateTime(),
    val location: String,
    val contact: String?,
    val stages: List<StageDetail> = arrayListOf(),
)

data class MaterialUpdateRequest(
    val materials: List<MaterialData>
)
data class LabourUpdateRequest(
    val labours: List<LabourData>
)

data class StageDetail(
    val _id: String,
    val id: String,
    val name: String,
    val createdDate: String = getDateTime(),
    val labourIds: List<String> = arrayListOf(),
    val materialIds: List<String> = arrayListOf(),
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