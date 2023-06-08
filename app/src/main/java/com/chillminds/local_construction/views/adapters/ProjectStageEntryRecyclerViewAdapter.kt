package com.chillminds.local_construction.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chillminds.local_construction.databinding.StageEntryViewBinding
import com.chillminds.local_construction.repositories.remote.dto.ProjectStageDetail
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecordList
import com.chillminds.local_construction.utils.dateConversion

class ProjectStageEntryRecyclerViewAdapter(
    private val lifeCycle: LifecycleOwner,
    private val dateKeysPairList: List<Pair<String, Long>>,
    private val dataList: Map<String, List<StageEntryRecord>>,
    val stageDetails: ProjectStageDetail
) : RecyclerView.Adapter<ProjectStageEntryRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: StageEntryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            date: String,
            recordList: List<StageEntryRecord>,
            lifeCycle: LifecycleOwner,
            stageDetails: ProjectStageDetail,
            isLastElement: Boolean,
        ) {
            binding.date = date.dateConversion()
            binding.totalPrice = recordList.sumOf { it.totalPrice }.toString()
            binding.stageEntryRecordList = StageEntryRecordList(recordList)
            binding.isLastElement = isLastElement
            binding.stageDetails = stageDetails
            binding.lifeCycle = lifeCycle
            binding.lifecycleOwner = lifeCycle
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        StageEntryViewBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(
            dateKeysPairList[position].first,
            dataList[dateKeysPairList[position].first] ?: arrayListOf(),
            lifeCycle,
            stageDetails,
            dataList.size == position + 1
        )

    override fun getItemCount(): Int = dataList.size

}
