package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillminds.local_construction.R
import com.chillminds.local_construction.databinding.FragmentStageTypeWiseStatisticsListBinding
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.fragments.StagesEntriesFragment
import org.koin.android.ext.android.inject

class StageTypeWiseStatisticsListFragment : Fragment() {

    var position: Int = 0

    lateinit var binding : FragmentStageTypeWiseStatisticsListBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStageTypeWiseStatisticsListBinding.inflate(inflater,container, false)
        position = arguments?.getInt("position") ?: 0
        binding.viewModel = viewModel
        binding.position = position
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    companion object {
        fun newInstance(position: Int?): StageTypeWiseStatisticsListFragment {
            val args = Bundle()
            args.putInt("position", position ?: 0)
            val f = StageTypeWiseStatisticsListFragment()
            f.arguments = args
            return f
        }
    }


}