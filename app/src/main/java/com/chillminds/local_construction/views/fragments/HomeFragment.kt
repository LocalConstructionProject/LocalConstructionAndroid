package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentHomeBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.repositories.remote.dto.ProjectStageDetail
import com.chillminds.local_construction.repositories.remote.dto.StageDetail
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.utils.validate
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.ProjectSpinnerAdapter
import com.chillminds.local_construction.views.adapters.ProjectStagesTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.spinner.InputSpinner
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
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

        binding.projectSpinner.adapter =
            ProjectSpinnerAdapter(requireActivity(), viewModel.commonModel.projectList.value)

        binding.projectSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val selectedProject =
                        parent.getItemAtPosition(position) as ProjectDetail
                    viewModel.selectProject(selectedProject)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        observeFields()
    }

    private fun setupTabBar(projectDetail: ProjectDetail) {
        val tabAdapter = ProjectStagesTabAdapter(this, projectDetail)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = projectDetail.stages.map { it.name }[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val project = viewModel.commonModel.selectedProjectDetail.value
                val stage = project?.stages?.elementAtOrNull(tab?.position ?: 0)
                viewModel.projectStagesTabAdapterPosition.postValue(stage)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeFields() {
        viewModel.commonModel.selectedProjectDetail.observe(viewLifecycleOwner) {
            if (it != null) {
                setupTabBar(it)
            }
        }
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_CREATE_STAGE_DIALOG -> {
                        showStageCreationBottomSheet()
                    }
                    Actions.SHOW_SHEET_TO_CHOOSE_OPTION_ON_HOME -> {
                        showChoiceOptionSheet()
                    }
                    Actions.SHOW_STAGE_ENTRY_EDIT_DIALOG -> {
                        viewModel.stageEntryDataToEdit.value?.validate()?.let { pairRecord ->
                            viewModel.updateEntryInformation(pairRecord.first)
                            StageEntryBottomSheet.show(parentFragmentManager)
                        } ?: kotlin.run {
                            viewModel.commonModel.actionListener.postValue("Failed to edit entry.")
                        }
                    }
                    Actions.SHOW_STAGE_ENTRY_DELETE_DIALOG -> {
                        viewModel.stageEntryDataToEdit.value?.validate()?.let { pairRecord ->
                            showDeleteConfirmationDialog(pairRecord)
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(pairRecord: Pair<StageEntryRecord, ProjectStageDetail>) {
        pairRecord.first.let { entry ->
            InfoSheet().show(requireActivity()) {
                title("Are you sure?")
                this.content("Do you really want to delete ${entry.name}-(${entry.count})? It you want to continue deletion, press ok.")
                onNegative { viewModel.commonModel.showSnackBar("Entry deletion Cancelled") }
                onPositive {
                    val records = pairRecord.second.entryRecords.filter { it._id != entry._id }
                    pairRecord.second.entryRecords = records
                    viewModel.commonModel.selectedProjectDetail.value?.let { projectDetail ->
                        entryDeletionApiCall(projectDetail, pairRecord.second)
                    }
                }
            }
        }
    }

    private fun entryDeletionApiCall(
        project: ProjectDetail,
        stage: ProjectStageDetail
    ) {
        viewModel.updateStage(project, stage).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.showProgress()
                }
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Failed to delete an entry.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Entry Record deleted Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                }
            }
        }
    }


        private fun showChoiceOptionSheet() {
            OptionSheet().show(requireActivity()) {
                title("Choose any one")
                with(
                    Option("Stage"),
                    Option("Entry Record"),
                )
                onPositive { index: Int, _: Option ->
                    if (index == 0) {
                        showStageCreationBottomSheet()
                    } else {
                        viewModel.updateEntryInformation()
                        StageEntryBottomSheet.show(parentFragmentManager)
                    }
                }
            }
        }

        private fun showStageCreationBottomSheet() {
            val selectedProjectDetail = viewModel.commonModel.selectedProjectDetail.value
            Pair(
                selectedProjectDetail,
                viewModel.commonModel.stagesData.value?.filter { stageDetails ->
                    stageDetails.name !in (selectedProjectDetail?.stages?.map { it.name }
                        ?: arrayListOf())
                }
            ).validate()?.let { (project, stages) ->
                InputSheet().show(requireActivity()) {
                    title("Stage Creation")
                    with(InputSpinner {
                        required()
                        this.options(listOf("Select Stage") + stages.map { it.name })
                        label("select Stage Name ")
                    })
                    onNegative { viewModel.commonModel.showSnackBar("Stage Creation Cancelled") }
                    onPositive { result ->
                        val index = result.getInt("0")
                        if (index == 0) {
                            viewModel.commonModel.showSnackBar("Select a stage")
                            return@onPositive
                        }
                        val selectedStage = stages[index - 1]
                        createStageUnderSelectedProject(project, selectedStage)
                    }
                }
            } ?: run {
                viewModel.commonModel.showSnackBar("Failed to create a stage")
            }
        }

        private fun createStageUnderSelectedProject(
            project: ProjectDetail,
            stage: StageDetail
        ) {
            viewModel.createStage(project, stage).observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiCallStatus.LOADING -> {
                        viewModel.commonModel.showProgress()
                    }
                    ApiCallStatus.ERROR -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Failed to create a stage.")
                    }
                    ApiCallStatus.SUCCESS -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Stage Created Successfully.")
                        viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                    }
                }
            }
        }
    }
