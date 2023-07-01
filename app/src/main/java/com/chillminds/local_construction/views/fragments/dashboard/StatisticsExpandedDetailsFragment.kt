package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillminds.local_construction.databinding.FragmentStatisticsExpandedDetailsBinding
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.android.ext.android.inject

class StatisticsExpandedDetailsFragment : Fragment() {

    val viewModel by inject<DashboardViewModel>()

    lateinit var binding : FragmentStatisticsExpandedDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsExpandedDetailsBinding.inflate(inflater, container, false)
        binding.dashboardData = viewModel.selectedDashboardStatistics.value
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }
}
