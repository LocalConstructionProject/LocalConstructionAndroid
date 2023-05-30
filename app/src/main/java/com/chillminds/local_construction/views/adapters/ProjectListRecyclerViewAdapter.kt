package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.ProjectListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent.inject

class ProjectListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<ProjectDetail>
) : RecyclerView.Adapter<ProjectListRecyclerViewAdapter.ViewHolder>() {

    private val viewModel: DashboardViewModel by inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: ProjectListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: ProjectDetail,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ProjectListRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle, viewModel
    )

    override fun getItemCount(): Int = dataList.size
}