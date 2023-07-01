package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.databinding.FragmentProjectDashboardBinding
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.ProjectDashBoardTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject

class ProjectDashboardFragment : Fragment() {

    lateinit var binding: FragmentProjectDashboardBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabBar()
        observeActionsListener()
    }

    private fun observeActionsListener() {
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_DASHBOARD_STATISTICS_EXPANSION_DETAILS ->
                        if (findNavController().currentDestination?.id == R.id.projectDashboardFragment) {
                            findNavController().navigate(R.id.action_projectDashboardFragment_to_statisticsExpandedDetailsFragment)
                        }
                }
            }
        }
    }

    private fun setTabBar() {
        val tabAdapter =
            ProjectDashBoardTabAdapter(this, viewModel.commonModel.dashboardProjectDetail.value)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = Constants.projectDashboardTabBar[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.projectDashboardPosition.value = (tab?.position ?: -1)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

}