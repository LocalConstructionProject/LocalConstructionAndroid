package com.chillminds.local_construction.views.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentPaymentUpsertBottomSheetBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.PaymentType
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.repositories.remote.dto.ProjectPaymentDetail
import com.chillminds.local_construction.utils.format
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.utils.toCalendar
import com.chillminds.local_construction.utils.toDateBelowOreo
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maxkeppeler.sheets.calendar.CalendarMode
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import org.koin.android.ext.android.inject
import java.util.*

class PaymentDetailUpsertBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentPaymentUpsertBottomSheetBinding

    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentUpsertBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialEntrySpinner

        binding.materialEntrySpinner.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.simple_list_item_1,
            PaymentType.values().map { it.name }
        )

        binding.materialEntrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val stageEntryRecord =
                        parent.getItemAtPosition(position) as String
                    viewModel.apply {
                        paymentType.postValue(PaymentType.valueOf(stageEntryRecord))
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
                    Actions.DISMISS_PAYMENT_ENTRY_BOTTOM_SHEET -> {
                        dismiss()
                    }

                    Actions.SHOW_DATE_BOTTOM_SHEET_FOR_PAYMENT_DATE -> {
                        showDateSheet()
                    }

                    Actions.INSERT_OR_VALIDATE_PAYMENT_ENTRY -> {
                        validateMaterialEntry()
                    }
                }
            }
        }
    }

    private fun showDateSheet() {
        CalendarSheet().show(requireActivity()) {
            title("Date of Payment.")
            this.calendarMode(CalendarMode.MONTH)
            this.selectionMode(SelectionMode.DATE)
            viewModel.projectPaymentDetailToUpdate.value?.let {
                it.dateOfPayment.toDateBelowOreo().toCalendar()
                    ?.let { it1 -> this.setSelectedDate(it1) }
            } ?: kotlin.run {
                this.setSelectedDate(Calendar.getInstance())
            }

            onPositive { dateStart, _ ->
                viewModel.paymentDate.postValue(dateStart.time.format())
            }
        }
    }

    private fun validateMaterialEntry() {
        val currentProject = viewModel.commonModel.selectedProjectDetail.value

        if (currentProject == null) {
            viewModel.commonModel.showSnackBar("Something went wrong. Try again Later")
            dismiss()
            return
        }

        val paymentType = viewModel.paymentType.value
        val date = viewModel.paymentDate.value
        val amount = viewModel.paymentValue.value

        val totalPrice = (amount?.toLongOrNull() ?: 0)

        if (totalPrice <= 0) {
            viewModel.commonModel.showSnackBar("Enter valid amount")
            return
        }

        if (date == null) {
            viewModel.commonModel.showSnackBar("select a valid date")
            return
        }

        val paymentDetail = viewModel.projectPaymentDetailToUpdate.value?.let { entry ->
            entry.apply {
                this.paymentType = paymentType ?: PaymentType.CASH
                this.payment = totalPrice
                this.dateOfPayment = date
            }
            entry
        } ?: kotlin.run {
            ProjectPaymentDetail(paymentType = paymentType ?: PaymentType.CASH,
                payment = totalPrice,
                dateOfPayment = date)
        }

        updatePaymentUnderSelectedProject(
            viewModel.commonModel.selectedProjectDetail.value!!,
            paymentDetail
        )
    }

    private fun updatePaymentUnderSelectedProject(
        project: ProjectDetail,
        paymentDetails: ProjectPaymentDetail
    ) {
        viewModel.updatePayment(project, paymentDetails).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.showProgress()
                }

                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Failed to update a Payment.")
                    dismiss()
                }

                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Payment update Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PROJECT_LIST)
                    dismiss()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun show(fragmentManager: FragmentManager) =
            PaymentDetailUpsertBottomSheet().show(
                fragmentManager,
                PaymentDetailUpsertBottomSheet::class.java.name
            )
    }
}