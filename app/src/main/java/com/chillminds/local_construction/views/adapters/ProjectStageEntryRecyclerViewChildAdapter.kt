package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.StageEntriesChildAdapterBinding
import com.chillminds.local_construction.repositories.remote.dto.ProjectStageDetail
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class ProjectStageEntryRecyclerViewChildAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<StageEntryRecord>,
    val stageDetails: ProjectStageDetail
) : RecyclerView.Adapter<ProjectStageEntryRecyclerViewChildAdapter.ViewHolder>() {

    private val viewModel: DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: StageEntriesChildAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: StageEntryRecord,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
            stageDetails: ProjectStageDetail,
        ) {
            binding.data = data
            binding.stageDetails = stageDetails
            binding.viewModel = viewModel
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        StageEntriesChildAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle, viewModel,
        stageDetails
    )

    override fun getItemCount(): Int = dataList.size
}
