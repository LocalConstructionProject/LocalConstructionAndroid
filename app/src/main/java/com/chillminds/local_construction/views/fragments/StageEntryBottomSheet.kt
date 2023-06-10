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
import com.chillminds.local_construction.utils.*
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.StageEntrySpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maxkeppeler.sheets.calendar.CalendarMode
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import org.koin.android.ext.android.inject
import java.util.*

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
                    viewModel.apply {
                        count.postValue(stageEntryRecord.count.toString())
                        price.postValue(stageEntryRecord.priceForTheDay.toString())
                        newMaterialEntrySpinnerSelection.postValue(stageEntryRecord)
                    }

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
                    Actions.SHOW_DATE_BOTTOM_SHEET -> {
                        showDateSheet()
                    }
                    Actions.INSERT_OR_VALIDATE_ENTRY -> {
                        validateMaterialEntry()
                    }
                }
            }
        }
    }

    private fun showDateSheet() {
        CalendarSheet().show(requireActivity()) {
            title("Date of execution.")
            this.calendarMode(CalendarMode.MONTH)
            this.selectionMode(SelectionMode.DATE)
            viewModel.materialEntryRecord.value?.let {
                it.dateOfExecution.toDateBelowOreo().toCalendar()
                    ?.let { it1 -> this.setSelectedDate(it1) }
            } ?: kotlin.run {
                this.setSelectedDate(Calendar.getInstance())
            }

            onPositive { dateStart, _ ->
                viewModel.date.postValue(dateStart.time.format())
            }
        }
    }

    private fun validateMaterialEntry() {
        val currentStage = viewModel.projectStagesTabAdapterPosition.value

        if (currentStage == null) {
            viewModel.commonModel.showSnackBar("Something went wrong. Try again Later")
            dismiss()
            return
        }

        val newEntry =
            viewModel.materialEntryRecord.value ?: viewModel.newMaterialEntrySpinnerSelection.value
        val count = viewModel.count.value
        val price = viewModel.price.value
        val date = viewModel.date.value

        val toAppendWithDate = newEntry?.dateOfExecution?.substring(10)

        val totalPrice =
            (viewModel.count.value?.toIntOrNull() ?: 1) * (viewModel.price.value?.toIntOrNull()
                ?: 0)
        if (count.isNullOrEmptyOrBlank() || count == "0" || count?.toLongOrNull() == null) {
            viewModel.commonModel.showSnackBar("Enter valid count")
            return
        }

        if (date == null) {
            viewModel.commonModel.showSnackBar("select a valid date")
            return
        }

        newEntry?.apply {
            this.count = count.toLongOrNull() ?: 0L
            this.totalPrice = totalPrice.toLong()
            this.priceForTheDay = price?.toLongOrNull() ?: 0L
            this.dateOfExecution = date.dateConversionReverse() + toAppendWithDate
        }
        val entryRecords = ArrayList(currentStage.entryRecords)
        entryRecords.add(newEntry)
        currentStage.entryRecords = entryRecords
        updateStageUnderSelectedProject(
            viewModel.commonModel.selectedProjectDetail.value!!,
            currentStage
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
                    dismiss()
                    viewModel.commonModel.showSnackBar("Failed to update a stage.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.showSnackBar("Stage update Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                    dismiss()
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