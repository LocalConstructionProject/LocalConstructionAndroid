package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.ExpandedDashboardEntriesChildAdapterBinding
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent.inject

class ProjectExpandedStatisticsRecyclerViewChildAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<StageEntryRecord>,
) : RecyclerView.Adapter<ProjectExpandedStatisticsRecyclerViewChildAdapter.ViewHolder>() {

    val viewModel: DashboardViewModel by inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: ExpandedDashboardEntriesChildAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: StageEntryRecord,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ExpandedDashboardEntriesChildAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle, viewModel,
    )

    override fun getItemCount(): Int = dataList.size
}

