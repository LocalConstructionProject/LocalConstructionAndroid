package com.chillminds.local_construction.repositories.remote.dto

import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.getDateTime
import com.google.gson.annotations.SerializedName
import java.util.*

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
    var amount: String,
    val id: String,
) {
    fun toStageEntry(count: Double = 0.0, totalPrice: Double = 0.0) =
        StageEntryRecord(
            name = this.name,
            stageTypeId = this.id,
            type = StageEntryType.MATERIAL,
            count = count,
            priceForTheDay = amount.toLong(),
            totalPrice = totalPrice
        )
}

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
) {
    fun toStageEntry(count: Double = 0.0, totalPrice: Double = 0.0) =
        StageEntryRecord(
            name = this.name,
            stageTypeId = this.id,
            type = StageEntryType.LABOUR,
            count = count,
            priceForTheDay = price.toLong(),
            totalPrice = totalPrice
        )
}

data class ProjectDetail(
    @SerializedName("_id") val id: String,
    var name: String,
    val createdDate: String = getDateTime(),
    var location: String,
    var contact: Long?,
    var stages: List<ProjectStageDetail> = arrayListOf(),
)

data class ProjectCreationRequest(
    val _id: UUID = UUID.randomUUID(),
    val name: String,
    val createdDate: String = getDateTime(),
    val location: String,
    val contact: Long?,
    val stages: List<ProjectStageDetail> = arrayListOf(),
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

data class ProjectStageDetail(
    var name: String,
    var id: UUID = UUID.randomUUID(),
    var startedDate: String = getDateTime(),
    var entryRecords: List<StageEntryRecord> = arrayListOf(),
)

data class ListInfo(
    var count: String,
    var totalPrice: String,
    var labourCount: String,
    var materialCount: String,
    val others: List<Pair<String, String>>
)

data class StageEntryRecord(
    val _id: UUID = UUID.randomUUID(),
    val name: String,
    val stageTypeId: String,
    val type: StageEntryType,
    val insertedDate: String = getDateTime(),
    var dateOfExecution: String = getDateTime(),
    var count: Double,
    var priceForTheDay: Long,
    var totalPrice: Double,
) {

    fun getDate() = dateOfExecution.dateConversion()

    fun toDashboardStatisticsDetails(
        projectDetail: ProjectDetail,
        stageDetail: ProjectStageDetail,
        entries: List<StageEntryRecord>,
    ) = DashboardStatisticsDetails(
        projectDetail.id,
        projectDetail.name,
        stageDetail.id,
        stageDetail.name,
        this,
        this.name,
        entries,
        this.count,
        this.totalPrice
    )
}

data class DashboardStatisticsDetails(
    val projectId: String,
    val projectName: String,
    val stageId: UUID,
    val stageName: String,
    val stageEntry: StageEntryRecord,
    val entryName: String,
    val entries: List<StageEntryRecord>,
    val count: Double,
    var totalPrice: Double,
)

data class StageEntryRecordList(
    var entryRecords: List<StageEntryRecord> = arrayListOf(),
)

enum class StageEntryType {
    MATERIAL, LABOUR
}