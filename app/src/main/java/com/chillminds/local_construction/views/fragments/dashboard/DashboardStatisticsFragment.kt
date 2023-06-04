package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.databinding.FragmentDashboardStatisticsBinding
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.android.ext.android.inject

class DashboardStatisticsFragment : Fragment() {

    lateinit var binding: FragmentDashboardStatisticsBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardStatisticsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dashboardSpinner.apply {
            adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                Constants.dashboardList
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    viewModel.spinnerSelectedPosition.postValue(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}