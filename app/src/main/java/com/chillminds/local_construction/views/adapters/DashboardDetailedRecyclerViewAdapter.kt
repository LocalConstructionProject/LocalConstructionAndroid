package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.ExpandedStatisticsEntryViewBinding
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecordList

class DashboardDetailedRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dataList: Map<String, List<StageEntryRecord>>,
    private val keys: Set<String>
) : RecyclerView.Adapter<DashboardDetailedRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: ExpandedStatisticsEntryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            date: String,
            recordList: List<StageEntryRecord>,
            lifeCycle: LifecycleOwner,
            isLastElement: Boolean,
        ) {
            binding.title = date
            binding.count = recordList.sumOf { it.count }.toString()
            binding.totalPrice = recordList.sumOf { it.totalPrice }.toString()
            binding.stageEntryRecordList = StageEntryRecordList(recordList)
            binding.isLastElement = isLastElement
            binding.lifeCycle = lifeCycle
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ExpandedStatisticsEntryViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(
            keys.elementAt(position),
            dataList[keys.elementAt(position)] ?: arrayListOf(),
            lifeCycle,
            dataList.size == position + 1
        )

    override fun getItemCount(): Int = dataList.size

}
