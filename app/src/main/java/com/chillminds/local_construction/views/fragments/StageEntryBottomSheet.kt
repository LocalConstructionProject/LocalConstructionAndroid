package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.FragmentManager
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentStageEntryBottomSheetBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.repositories.remote.dto.ProjectStageDetail
import com.chillminds.local_construction.repositories.remote.dto.StageEntryRecord
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.StageEntrySpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class StageEntryBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentStageEntryBottomSheetBinding

    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStageEntryBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialEntrySpinner

        // val currentStage = viewModel.projectStagesTabAdapterPosition.value
        val materialList = viewModel.commonModel.materialData.value ?: arrayListOf()
        val labourList = viewModel.commonModel.labourData.value ?: arrayListOf()

        val dataList = materialList.map { it.toStageEntry() } + labourList.map { it.toStageEntry() }
        // val options = dataList.map { it.name + " - " + it.priceForTheDay }

        binding.materialEntrySpinner.adapter =
            StageEntrySpinnerAdapter(requireActivity(), dataList)

        binding.materialEntrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val stageEntryRecord =
                        parent.getItemAtPosition(position) as StageEntryRecord
                    // viewModel.selectProject(selectedProject)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        observeFields()

    }

    private fun observeFields() {
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.DISMISS_STAGE_ENTRY_BOTTOM_SHEET -> {
                        dismiss()
                    }
                    Actions.INSERT_OR_VALIDATE_ENTRY -> {
                        validateMaterialEntry()
                    }
                }
            }
        }
    }

    private fun validateMaterialEntry() {
        val currentStage = viewModel.projectStagesTabAdapterPosition.value
        val newEntry = viewModel.materialEntryRecord.value
        val count = viewModel.count.value
        val price = viewModel.price.value
        val date = viewModel.date.value
        val totalPrice = (viewModel.count.value?.toIntOrNull()?:1) * (viewModel.price.value?.toIntOrNull()?:0)
        if (count.isNullOrEmptyOrBlank() && count == "0" && count.toLongOrNull() == null) {
            viewModel.commonModel.showSnackBar("Enter valid count")
            return
        }
        newEntry?.apply {
            this.count = count?.toLongOrNull() ?: 0L
            this.totalPrice = totalPrice.toLong()
            this.priceForTheDay = price?.toLongOrNull() ?: 0L
        }
        val entryRecords = ArrayList(currentStage?.entryRecords ?: arrayListOf())
        entryRecords.add(newEntry)
        currentStage?.entryRecords = entryRecords
        updateStageUnderSelectedProject(
            viewModel.commonModel.selectedProjectDetail.value!!,
            currentStage!!
        )
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

    companion object {
        @JvmStatic
        fun show(fragmentManager: FragmentManager) =
            StageEntryBottomSheet().show(fragmentManager, StageEntryBottomSheet::class.java.name)
    }
}