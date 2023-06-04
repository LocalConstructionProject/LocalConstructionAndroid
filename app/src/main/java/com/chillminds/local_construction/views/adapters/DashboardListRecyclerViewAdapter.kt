package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.DashboardListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.DashboardStatisticsDetails
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class DashboardListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<DashboardStatisticsDetails>
) : RecyclerView.Adapter<DashboardListRecyclerViewAdapter.ViewHolder>() {
    private val viewModel: DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: DashboardListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: DashboardStatisticsDetails,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
            isLastIndex: Boolean
        ) {
            binding.data = data
            binding.isLastIndex = isLastIndex
            binding.viewModel = viewModel
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DashboardListRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle,
        viewModel, dataList.size == position + 1
    )

    override fun getItemCount(): Int = dataList.size
}