package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.RentalInformationListRecyclerViewBinding
import com.chillminds.local_construction.databinding.RentalProductListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.RentalInformation
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class RentalInformationListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<RentalInformation>
) : RecyclerView.Adapter<RentalInformationListRecyclerViewAdapter.ViewHolder>() {

    private val viewModel: DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: RentalInformationListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: RentalInformation,
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
        RentalInformationListRecyclerViewBinding.inflate(
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