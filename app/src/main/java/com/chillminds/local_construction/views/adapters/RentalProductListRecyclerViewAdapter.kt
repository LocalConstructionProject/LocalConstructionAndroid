package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.RentalProductListRecyclerViewBinding
import com.chillminds.local_construction.repositories.remote.dto.RentalProduct
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.java.KoinJavaComponent

class RentalProductListRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: List<RentalProduct>
) : RecyclerView.Adapter<RentalProductListRecyclerViewAdapter.ViewHolder>() {

    private val viewModel: DashboardViewModel by KoinJavaComponent.inject(DashboardViewModel::class.java)

    class ViewHolder(val binding: RentalProductListRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: RentalProduct,
            lifeCycle: LifecycleOwner,
            viewModel: DashboardViewModel,
            isLastIndex: Boolean
        ) {
            binding.data = data
            binding.viewModel = viewModel
            binding.isLastIndex = isLastIndex
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        RentalProductListRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(
        dataList[position],
        lifeCycle, viewModel, dataList.size == position + 1
    )

    override fun getItemCount(): Int = dataList.size
}