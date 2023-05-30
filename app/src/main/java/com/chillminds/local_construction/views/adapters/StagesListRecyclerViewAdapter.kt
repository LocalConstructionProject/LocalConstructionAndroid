package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.StagesListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.StageDetail
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class StagesListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<StageDetail>
) : RecyclerView.Adapter<StagesListRecyclerViewAdapter.ViewHolder>() {

    private val viewModel : DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: StagesListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: StageDetail,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.lifeCycle = lifeCycle
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        StagesListRecyclerViewBinding.inflate(
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