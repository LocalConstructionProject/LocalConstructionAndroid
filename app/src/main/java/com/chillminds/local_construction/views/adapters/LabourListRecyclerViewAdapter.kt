package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.LabourListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.LabourData
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class LabourListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<LabourData>
) : RecyclerView.Adapter<LabourListRecyclerViewAdapter.ViewHolder>() {
    private val viewModel : DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: LabourListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: LabourData,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LabourListRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle,
        viewModel
    )

    override fun getItemCount(): Int = dataList.size
}