package com.chillminds.local_construction.views.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.repositories.remote.dto.LabourData
import com.chillminds.local_construction.repositories.remote.dto.MaterialData
import com.chillminds.local_construction.repositories.remote.dto.StageDetail
import com.chillminds.local_construction.views.adapters.LabourListRecyclerViewAdapter
import com.chillminds.local_construction.views.adapters.MaterialListRecyclerViewAdapter
import com.chillminds.local_construction.views.adapters.StagesListRecyclerViewAdapter

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


