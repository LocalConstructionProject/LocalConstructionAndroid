package com.chillminds.local_construction.views.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.repositories.remote.dto.*
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

@BindingAdapter("lifeCycle", "setupProjectDashboardInformation", requireAll = false)
fun setupProjectDashboardInformation(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    projectDetail: ProjectDetail?
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()
    projectDetail?.stages?.forEach { stageDetails ->
        dashboardEntryDetails.addAll(stageDetails.entryRecords.map {
            it.toDashboardStatisticsDetails(
                projectDetail,
                stageDetails
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
                    entries.sumOf { it.count },
                    entries.sumOf { it.totalPrice })
            }
        }

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, listToDisplay)
}

@BindingAdapter("lifeCycle", "setupDashboardInformation", requireAll = false)
fun setupDashboardInformation(
    recyclerView: RecyclerView,
    lifeCycle: LifecycleOwner,
    projectList: List<ProjectDetail>?
) {

    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
    val dashboardEntryDetails = arrayListOf<DashboardStatisticsDetails>()
    projectList?.forEach { projectDetail ->
        projectDetail.stages.forEach { stageDetails ->
            dashboardEntryDetails.addAll(stageDetails.entryRecords.map {
                it.toDashboardStatisticsDetails(
                    projectDetail,
                    stageDetails
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
                    entries.sumOf { it.count },
                    entries.sumOf { it.totalPrice })
            }
        }

    recyclerView.adapter =
        DashboardListRecyclerViewAdapter(lifeCycle, listToDisplay)
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


