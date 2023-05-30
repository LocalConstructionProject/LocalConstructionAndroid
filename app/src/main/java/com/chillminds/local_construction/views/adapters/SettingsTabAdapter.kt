package com.chillminds.local_construction.views.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chillminds.local_construction.views.fragments.settings.LabourListFragment
import com.chillminds.local_construction.views.fragments.settings.MaterialListFragment
import com.chillminds.local_construction.views.fragments.settings.StagesListFragment

class SettingsTabAdapter(booksTabFragment: Fragment) : FragmentStateAdapter(booksTabFragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> LabourListFragment()
            0 -> MaterialListFragment()
            else -> StagesListFragment()
        }
    }
}
