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

data class RentalProductDetailsResponse(
    val status: String,
    val statusCode: Int,
    val data: List<RentalProduct> = arrayListOf(),
)

data class RentalProduct(
    @SerializedName("_id") val id: String,
    val name: String,
    val quantity: Int,
    val rentalPrice: Int,
    val rentalType: RentalType = RentalType.Daily
) {
    fun toUpdate() = UpdateRentalProduct(name, quantity, rentalPrice)
}

enum class RentalType {
    Daily, Monthly
}

data class UpdateRentalProduct(
    val name: String,
    val quantity: Int,
    val rentalPrice: Int,
)

data class RentalDataListResponse(
    val status: String,
    val statusCode: Int,
    val data: List<RentalInformation> = arrayListOf(),
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
    var paymentDetails: List<ProjectPaymentDetail> = arrayListOf(),
)

data class RentalInformation(
    @SerializedName("_id") var id: String? = "",
    val productName: String,
    val productId: String,
    var rentedDate: String = "",
    val customerName: String,
    val phoneNumber: String,
    val place: String,
    val advanceAmount: Int,
    val finalPayment: Int? = 0,
    var productStatus: String = "Rented",
    var returnDetails: List<ReturnDetails> = arrayListOf(),
    val productCount: Int,
) {
    fun toUpdate() = UpdateRentalInformation(
        productName,
        productId,
        rentedDate,
        customerName,
        phoneNumber,
        place,
        advanceAmount,
        finalPayment,
        productStatus,
        productCount,
        returnDetails
    )
}

data class ReturnDetails(
    @SerializedName("_id") val id: UUID = UUID.randomUUID(),
    var returnProductCount: Int? = 0,
    var returnDate: String = "",
)

data class UpdateRentalInformation(
    val productName: String,
    val productId: String,
    var rentedDate: String = "",
    val customerName: String,
    val phoneNumber: String,
    val place: String,
    val advanceAmount: Int,
    val finalPayment: Int? = 0,
    var productStatus: String = "Rented",
    val productCount: Int,
    var returnDetails: List<ReturnDetails> = arrayListOf(),
)

data class ProjectCreationRequest(
    val _id: UUID = UUID.randomUUID(),
    val name: String,
    val createdDate: String = getDateTime(),
    val location: String,
    val contact: Long?,
    val stages: List<ProjectStageDetail> = arrayListOf(),
    var paymentDetails: List<ProjectPaymentDetail> = arrayListOf(),
)

data class RentalProductCreationRequest(
    val name: String,
    val quantity: Int,
    val rentalPrice: Int,
    val rentalType: String
)

data class ProjectPaymentDetail(
    @SerializedName("_id") val id: UUID = UUID.randomUUID(),
    var paymentType: PaymentType,
    var dateOfPayment: String,
    var payment: Long?,
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