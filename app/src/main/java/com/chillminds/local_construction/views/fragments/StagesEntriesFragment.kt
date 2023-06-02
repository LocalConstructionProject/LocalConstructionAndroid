package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.databinding.FragmentStagesEntriesBinding
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.android.ext.android.inject

class StagesEntriesFragment() : Fragment() {

    var position: Int = 0

    lateinit var binding: FragmentStagesEntriesBinding

    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStagesEntriesBinding.inflate(inflater, container, false)
        position = arguments?.getInt("position") ?: 0
        val stages = viewModel.commonModel.selectedProjectDetail.value?.stages
        val projectStageDetail = if (stages.isNullOrEmpty() || stages.size <= position) {
            null
        } else {
            stages[position]
        }

        binding.data = projectStageDetail
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    companion object {
        fun newInstance(position: Int?): StagesEntriesFragment {
            val args = Bundle()
            args.putInt("position", position ?: 0)
            val f = StagesEntriesFragment()
            f.arguments = args
            return f
        }
    }

}