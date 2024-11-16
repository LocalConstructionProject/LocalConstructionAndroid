package com.chillminds.local_construction.views.binding_adapters

import android.os.Build
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.repositories.remote.dto.*
import com.chillminds.local_construction.utils.countDaysBetween
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.getLocalDateTimeFormat
import com.chillminds.local_construction.utils.toDate
import com.chillminds.local_construction.utils.toDateBelowOreo
import com.chillminds.local_construction.views.adapters.*
import java.time.LocalDateTime
import java.util.*

@BindingAdapter("lifeCycle", "setLabourListAdapter", requireAll = false)
fun setLabourListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<LabourData>?
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        LabourListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())

}

@BindingAdapter("lifeCycle", "setProjectListAdapter", requireAll = false)
fun setProjectListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<ProjectDetail>?
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        ProjectListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())

}

@BindingAdapter("lifeCycle", "setRentalProductListAdapter", requireAll = false)
fun setRentalProductListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<RentalProduct>?
) {
    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        RentalProductListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())
}

@BindingAdapter("lifeCycle", "setRentalInformationListAdapter", requireAll = false)
fun setRentalInformationListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<RentalInformation>?
) {
    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        RentalInformationListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())
}

@BindingAdapter("lifeCycle", "setStagesEntryAdapter", "stageDetails", requireAll = false)
fun setStagesEntryAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    entryRecords: List<StageEntryRecord>?,
    stageDetails: ProjectStageDetail
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    val dateWiseRecords = entryRecords?.groupBy { it.dateOfExecution.subSequence(0, 10).toString() }
    val dateSet = (dateWiseRecords?.keys ?: setOf()).map { Pair(it, it.toDateBelowOreo().time) }
        .sortedBy { it.second }.reversed()

    recyclerView.adapter =
        ProjectStageEntryRecyclerViewAdapter(
            lifeCycle,
            dateSet,
            dateWiseRecords ?: hashMapOf(),
            stageDetails
        )

}

@BindingAdapter("lifeCycle", "setStagesEntryChildAdapter", "stageDetails", requireAll = false)
fun setStagesEntryChildAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    entryRecords: List<StageEntryRecord>?,
    stageDetails: ProjectStageDetail
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        ProjectStageEntryRecyclerViewChildAdapter(
            lifeCycle,
            entryRecords ?: arrayListOf(),
            stageDetails
        )

}

@BindingAdapter("lifeCycle", "setExpandedStatisticsChildAdapter", requireAll = false)
fun setExpandedStatisticsChildAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    entryRecords: List<StageEntryRecord>?,
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        ProjectExpandedStatisticsRecyclerViewChildAdapter(
            lifeCycle,
            entryRecords ?: arrayListOf(),
        )

}

@BindingAdapter("lifeCycle", "position", "setStatisticsDataAdapter", requireAll = false)
fun setStatisticsDataAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    position: Int,
    data: DashboardStatisticsDetails,
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    Logger.error("$position", "$data")

    val mapWithEntries = when (position) {
        1 -> data.entries.groupBy { it.dateOfExecution.dateConversion() }
        else -> data.entries.groupBy { it.name }
    }

    recyclerView.adapter =
        DashboardDetailedRecyclerViewAdapter(
            lifeCycle,
            mapWithEntries,
            when (position) {
                1 -> mapWithEntries.keys.sortedBy { it.toDateBelowOreo() }.toSet()
                else -> mapWithEntries.keys
            }
        )
}

@BindingAdapter("lifeCycle", "position", "setupProjectDashboardInformation", requireAll = false)
fun setupProjectDashboardInformation(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    position: Int,
    projectDetail: ProjectDetail?
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()
    projectDetail?.stages?.forEach { stageDetails ->
        val entries = stageDetails.entryRecords.filter {
            when (position) {
                0 -> it.type == StageEntryType.LABOUR || it.type == StageEntryType.MATERIAL
                1 -> it.type == StageEntryType.MATERIAL
                else -> it.type == StageEntryType.LABOUR
            }
        }
        dashboardEntryDetails.addAll(
            entries.map {
                it.toDashboardStatisticsDetails(
                    projectDetail,
                    stageDetails,
                    entries
                )
            })
    }

    val listToDisplay =
        dashboardEntryDetails.groupBy { it.entryName }.filter { it.value.isNotEmpty() }.map {
            val entryName = it.key
            val entries = it.value
            entries.first().let { data ->
                DashboardStatisticsDetails(
                    data.projectId,
                    data.projectName,
                    data.stageId,
                    data.stageName,
                    data.stageEntry,
                    entryName,
                    entries.map { it.stageEntry },
                    entries.sumOf { it.count },
                    entries.sumOf { it.totalPrice })
            }
        }

    val total = if (listToDisplay.isNotEmpty()) {
        arrayListOf(DashboardStatisticsDetails(
            "DUMMY",
            "ALL PROJECTS",
            UUID.randomUUID(),
            "ALL STAGES",
            listToDisplay.first().stageEntry,
            "TOTAL",
            arrayListOf(),
            listToDisplay.sumOf { it.count },
            listToDisplay.sumOf { it.totalPrice }
        ))
    } else arrayListOf()

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, listToDisplay + total)
}

@BindingAdapter("lifeCycle", "setupDashboardInformation", "sortByPosition", requireAll = false)
fun setupDashboardInformation(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    projectList: List<ProjectDetail>?,
    sortByPosition: Int
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()
    projectList?.forEach { projectDetail ->
        projectDetail.stages.forEach { stageDetails ->
            dashboardEntryDetails.addAll(stageDetails.entryRecords.map {
                it.toDashboardStatisticsDetails(
                    projectDetail,
                    stageDetails,
                    stageDetails.entryRecords
                )
            })
        }
    }

    val listToDisplay =
        dashboardEntryDetails.groupBy { it.entryName }.filter { it.value.isNotEmpty() }.map {
            val entryName = it.key
            val entries = it.value
            entries.first().let { data ->
                DashboardStatisticsDetails(
                    data.projectId,
                    data.projectName,
                    data.stageId,
                    data.stageName,
                    data.stageEntry,
                    entryName,
                    entries.map { it.stageEntry },
                    entries.sumOf { it.count },
                    entries.sumOf { it.totalPrice })
            }
        }

    val dataToDisplay = when (sortByPosition) {
        1 -> listToDisplay.sortedBy { it.count }
        2 -> listToDisplay.sortedBy { it.totalPrice }
        3 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.LABOUR }
            .sortedBy { it.stageId }

        4 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.MATERIAL }
            .sortedBy { it.stageId }

        else -> listToDisplay
    }
    val total = if (dataToDisplay.isNotEmpty()) {
        arrayListOf(DashboardStatisticsDetails(
            "DUMMY",
            "ALL PROJECTS",
            UUID.randomUUID(),
            "ALL STAGES",
            dataToDisplay.first().stageEntry,
            "TOTAL",
            arrayListOf(),
            dataToDisplay.sumOf { it.count },
            dataToDisplay.sumOf { it.totalPrice }
        ))
    } else arrayListOf()

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, dataToDisplay + total)
}

@BindingAdapter("lifeCycle", "setStagesLabourList", "stagesData", requireAll = false)
fun setStagesLabourList(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<LabourData>?,
    stagesData: StageDetail,
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dataList = (data ?: arrayListOf()).filter { it.id in stagesData.labourIds }
    recyclerView.adapter =
        LabourListRecyclerViewAdapter(lifeCycle, dataList)

}

@BindingAdapter("lifeCycle", "setStagesMaterialList", "stagesData", requireAll = false)
fun setStagesMaterialList(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<MaterialData>?,
    stagesData: StageDetail,
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dataList = (data ?: arrayListOf()).filter { it.id in stagesData.materialIds }
    recyclerView.adapter =
        MaterialListRecyclerViewAdapter(lifeCycle, dataList)

}

@BindingAdapter("lifeCycle", "setMaterialListAdapter")
fun setMaterialListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<MaterialData>?
) {
    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        MaterialListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())
}

@BindingAdapter("lifeCycle", "setStagesListAdapter")
fun setStagesListAdapter(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    data: List<StageDetail>?
) {
    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

    recyclerView.adapter =
        StagesListRecyclerViewAdapter(lifeCycle, data ?: arrayListOf())
}

@BindingAdapter("setBalanceAmount", "rentalInformation")
fun setBalanceAmount(
    textView: TextView,
    productList: List<RentalProduct>? = arrayListOf(),
    data: RentalInformation
) {
    val rentalPrice = productList?.find { it.id == data.productId }?.rentalPrice ?: 0

    val numberOfDays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        data.rentedDate.toDate(getLocalDateTimeFormat())
            .countDaysBetween(LocalDateTime.now())
    } else {
        data.rentedDate.toDateBelowOreo(getLocalDateTimeFormat())
            .countDaysBetween(
                Calendar.getInstance().time
            )
    }

    val priceForReturnedProduct = data.returnDetails.sumOf { returnDetails ->
        val daysCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            data.rentedDate.toDate(getLocalDateTimeFormat())
                .countDaysBetween(returnDetails.returnDate.toDate(getLocalDateTimeFormat()))
        } else {
            data.rentedDate.toDateBelowOreo(getLocalDateTimeFormat())
                .countDaysBetween(
                    returnDetails.returnDate.toDateBelowOreo(getLocalDateTimeFormat())
                )
        }
        daysCount * (returnDetails.returnProductCount ?: 0) * rentalPrice
    }

    val productToReturn =
        data.productCount - data.returnDetails.sumOf {
            it.returnProductCount ?: 0
        }

    val priceForNonReturnedProduct = productToReturn * rentalPrice * numberOfDays

    val balance = "₹ ${(priceForReturnedProduct + priceForNonReturnedProduct - data.advanceAmount)}"

    textView.text = balance
}

@BindingAdapter("rentalProduct", "setBalanceProductCount")
fun setBalanceProductCount(
    textView: TextView,
    rentalProduct: RentalProduct,
    rentalInfoList: List<RentalInformation>? = arrayListOf()
) {
    val totalRentalProduct = arrayListOf<RentalInformation>().apply {
        addAll(rentalInfoList ?: arrayListOf())
    }.filter { it.productId == rentalProduct.id }.sumOf { it.productCount }

    val totalReturnCount = arrayListOf<RentalInformation>().apply {
        addAll(rentalInfoList ?: arrayListOf())
    }.filter { it.productId == rentalProduct.id }
        .sumOf { it.returnDetails.sumOf { returnData -> returnData.returnProductCount ?: 0 } }

    val balance = "${rentalProduct.quantity - totalRentalProduct + totalReturnCount}"
    textView.text = balance
}
