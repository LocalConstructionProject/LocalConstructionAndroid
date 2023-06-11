package com.chillminds.local_construction.views.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.views.fragments.StagesEntriesFragment
import com.chillminds.local_construction.views.fragments.dashboard.DashboardStatisticsFragment
import com.chillminds.local_construction.views.fragments.dashboard.ProjectListFragment

class DashBoardTabAdapter(booksTabFragment: Fragment) : FragmentStateAdapter(booksTabFragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ProjectListFragment()
            else -> DashboardStatisticsFragment()
        }
    }
}

class ProjectStagesTabAdapter(booksTabFragment: Fragment, val projectDetail: ProjectDetail) :
    FragmentStateAdapter(booksTabFragment) {

    override fun getItemCount() = projectDetail.stages.size

    override fun createFragment(position: Int) = StagesEntriesFragment.newInstance(position)
}
