package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.databinding.FragmentHomeBinding
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.ProjectSpinnerAdapter
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.projectSpinner.adapter = ProjectSpinnerAdapter(requireActivity(),viewModel.commonModel.projectList.value)

        binding.projectSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    val clickedItem = parent.getItemAtPosition(position) as ProjectDetail
                    viewModel.selectProject(clickedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

}