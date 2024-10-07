package com.chillminds.local_construction.views.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.repositories.remote.dto.*
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.toDateBelowOreo
import com.chillminds.local_construction.views.adapters.*

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
            when(position){
                1-> mapWithEntries.keys.sortedBy { it.toDateBelowOreo() }.toSet()
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

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, listToDisplay)
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

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, when (sortByPosition) {
            1 -> listToDisplay.sortedBy { it.count }
            2 -> listToDisplay.sortedBy { it.totalPrice }
            3 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.LABOUR }
                .sortedBy { it.stageId }
            4 -> listToDisplay.filter { it.stageEntry.type == StageEntryType.MATERIAL }
                .sortedBy { it.stageId }
            else -> listToDisplay
        })
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


