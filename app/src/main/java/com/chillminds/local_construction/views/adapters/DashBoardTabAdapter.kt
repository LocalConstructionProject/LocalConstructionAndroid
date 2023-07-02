package com.chillminds.local_construction.views.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.views.fragments.StagesEntriesFragment
import com.chillminds.local_construction.views.fragments.dashboard.DashboardStatisticsFragment
import com.chillminds.local_construction.views.fragments.dashboard.ProjectListFragment
import com.chillminds.local_construction.views.fragments.dashboard.StageTypeWiseStatisticsListFragment

class DashBoardTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = Constants.dashboardDashList.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ProjectListFragment()
            else -> DashboardStatisticsFragment()
        }
    }
}

class ProjectStagesTabAdapter(fragment: Fragment, val projectDetail: ProjectDetail) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = projectDetail.stages.size

    override fun createFragment(position: Int) = StagesEntriesFragment.newInstance(position)
}

class ProjectDashBoardTabAdapter(
    fragment: Fragment,
    val projectDetail: ProjectDetail?
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = Constants.projectDashboardTabBar.size

    override fun createFragment(position: Int) =
        StageTypeWiseStatisticsListFragment.newInstance(position)
}
