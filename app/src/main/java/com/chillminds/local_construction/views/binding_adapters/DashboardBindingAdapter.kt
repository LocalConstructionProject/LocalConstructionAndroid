package com.chillminds.local_construction.views.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.repositories.remote.dto.*
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

    recyclerView.adapter =
        ProjectStageEntryRecyclerViewAdapter(lifeCycle, entryRecords ?: arrayListOf(),stageDetails)

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


