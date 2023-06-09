package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.text.InputType
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
import com.chillminds.local_construction.utils.getDateTime
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.utils.validate
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.ProjectSpinnerAdapter
import com.chillminds.local_construction.views.adapters.ProjectStagesTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
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
                            // showStageEntryDialog(pairRecord)
                            viewModel.updateEntryInformation(pairRecord.first)
                            StageEntryBottomSheet.show(parentFragmentManager)
                        } ?: kotlin.run {
                            viewModel.commonModel.actionListener.postValue("Failed to edit entry.")
                        }
                    }
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
                    // showCreateStageEntryDialog()
                }
            }
        }
    }

    private fun showCreateStageEntryDialog() {
        val currentStage = viewModel.projectStagesTabAdapterPosition.value
        val materialList = viewModel.commonModel.materialData.value ?: arrayListOf()
        val labourList = viewModel.commonModel.labourData.value ?: arrayListOf()

        val dataList = materialList.map { it.toStageEntry() } + labourList.map { it.toStageEntry() }
        val options = dataList.map { it.name + " - " + it.priceForTheDay }
        InputSheet().show(requireActivity()) {
            with(InputSpinner("entry") {
                title("New Entry to ${currentStage?.name}")
                options(options)
                changeListener {
                    dataList[it]
                }
            })
            with(InputEditText("count") {
                hint("Count")
                inputType(InputType.TYPE_CLASS_NUMBER)
            })
            with(InputEditText("price") {
                hint("Price")
                inputType(InputType.TYPE_CLASS_NUMBER)
            })
            with(InputEditText("totalPrice") {
                hint("Total Price")
                inputType(InputType.TYPE_CLASS_NUMBER)
            })
            onNegative { viewModel.commonModel.showSnackBar("Cancelled") }
            onPositive { result ->
                val materialEntry = result.getInt("entry")
                val count = result.getString("count")
                val price = result.getString("price") ?: "0"
                val totalPrice = result.getString("totalPrice") ?: "0"
                if (count.isNullOrEmptyOrBlank() && count == "0" && count.toLongOrNull() == null) {
                    viewModel.commonModel.showSnackBar("Enter valid count")
                    return@onPositive
                }
                val newEntry = dataList[materialEntry]
                newEntry.apply {
                    this.count = count?.toLongOrNull() ?: 0L
                    this.totalPrice = totalPrice.toLongOrNull() ?: 0L
                    this.priceForTheDay = price.toLongOrNull() ?: 0L
                }
                val entryRecords = ArrayList(currentStage?.entryRecords ?: arrayListOf())
                entryRecords.add(newEntry)
                currentStage?.entryRecords = entryRecords
                updateStageUnderSelectedProject(
                    viewModel.commonModel.selectedProjectDetail.value!!,
                    currentStage!!
                )
            }
        }
    }

    private fun showStageEntryDialog(pairRecord: Pair<StageEntryRecord, ProjectStageDetail>) {
        val stageEntryRecord = pairRecord.first
        InputSheet().show(requireActivity()) {
            title("${pairRecord.second.name} - ${pairRecord.first.name}")
            with(InputEditText("count") {
                hint("Count")
                inputType(InputType.TYPE_CLASS_NUMBER)
                this.defaultValue(stageEntryRecord.count.toString())
            })
            with(InputEditText("price") {
                hint("Price")
                inputType(InputType.TYPE_CLASS_NUMBER)
                this.defaultValue(stageEntryRecord.priceForTheDay.toString())
            })
            with(InputEditText("totalPrice") {
                hint("Total Price")
                inputType(InputType.TYPE_CLASS_NUMBER)
                this.defaultValue(stageEntryRecord.totalPrice.toString())
            })
            with(InputEditText("date") {
                hint("Date")
                inputType(InputType.TYPE_CLASS_TEXT)
                this.defaultValue(stageEntryRecord.dateOfExecution.subSequence(0,10))
            })
            onNegative { viewModel.commonModel.showSnackBar("Cancelled") }
            onPositive { result ->
                val now = getDateTime()
                val count = result.getString("count")
                val price = result.getString("price") ?: "0"
                val totalPrice = result.getString("totalPrice") ?: "0"
                val date = result.getString("date") ?: now.substring(0,10)
                if (count.isNullOrEmptyOrBlank() && count == "0" && count.toLongOrNull() == null) {
                    viewModel.commonModel.showSnackBar("Enter valid count")
                    return@onPositive
                }
                val projectStage = pairRecord.second
                projectStage.entryRecords.forEach {
                    if (it._id == stageEntryRecord._id) {
                        it.count = count?.toLongOrNull() ?: 0
                        it.priceForTheDay = price.toLongOrNull() ?: 0
                        it.totalPrice = totalPrice.toLongOrNull() ?: 0
                        it.dateOfExecution = date + now.substring(11)
                    }
                }

                updateStageUnderSelectedProject(
                    viewModel.commonModel.selectedProjectDetail.value!!,
                    projectStage
                )
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

                }
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.showSnackBar("Failed to create a stage.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.showSnackBar("Stage Created Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                }
            }
        }
    }

    private fun updateStageUnderSelectedProject(
        project: ProjectDetail,
        stage: ProjectStageDetail
    ) {
        viewModel.updateStage(project, stage).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {

                }
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.showSnackBar("Failed to update a stage.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.showSnackBar("Stage update Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                }
            }
        }
    }

}
