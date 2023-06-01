package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.MaterialListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.MaterialData
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent.inject

class MaterialListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<MaterialData>
) : RecyclerView.Adapter<MaterialListRecyclerViewAdapter.ViewHolder>() {

    private val viewModel: DashboardViewModel by inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: MaterialListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: MaterialData,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
            showEdit: Boolean
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.showEdit = showEdit
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        MaterialListRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle, viewModel, (viewModel.commonModel.materialData.value?.size ?: 0) == dataList.size
    )

    override fun getItemCount(): Int = dataList.size
}