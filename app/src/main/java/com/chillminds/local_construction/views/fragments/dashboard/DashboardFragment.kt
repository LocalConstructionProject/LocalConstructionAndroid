package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.databinding.FragmentDashboardBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.DashBoardTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import org.koin.android.ext.android.inject

class DashboardFragment : Fragment() {

    lateinit var binding: FragmentDashboardBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabBar()
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_PROJECT_CREATION_SHEET -> {
                        showProjectCreationSheet()
                    }
                }
            }
        }
    }

    private fun setTabBar() {
        val tabAdapter = DashBoardTabAdapter(this)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = Constants.dashboardDashList[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    private fun showProjectCreationSheet() {
        InputSheet().show(requireActivity()) {
            title("Create Project")
            with(InputEditText {
                required()
                label("Project Name *")
                hint("Name of the project.")
            })
            with(InputEditText {
                required()
                label("Project Location *")
                hint("Name of the place")
            })
            with(InputEditText {
                label("Contact Number")
                hint("Phone Number")
            })
            onNegative { viewModel.commonModel.showSnackBar("Project Creation Cancelled") }
            onPositive { result ->
                val name = result.getString("0")
                val location = result.getString("1")
                val contact = result.getString("2") ?: "0"
                if (name.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Project Name is mandatory")
                    return@onPositive
                }
                if (location.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("location Name is mandatory")
                    return@onPositive
                }

                createProject(name, location, contact)

            }
        }
    }

    private fun createProject(name: String?, location: String?, contact: String) {
        viewModel.createProject(name ?: "", location ?: "", contact).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.showSnackBar("Failed to create a project.")
                }
                ApiCallStatus.SUCCESS -> {
                    it.data
                    viewModel.commonModel.showSnackBar("Project Created Successfully.")
                }
            }
        }
    }

}