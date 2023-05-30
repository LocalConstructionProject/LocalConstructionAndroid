package com.chillminds.local_construction.views.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.databinding.FragmentStagesListBinding
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.android.ext.android.inject

class StagesListFragment : Fragment() {

    val viewModel by inject<DashboardViewModel>()
    lateinit var binding: FragmentStagesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStagesListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }
}
