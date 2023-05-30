package com.chillminds.local_construction.views.fragments.settings

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentLabourListBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.LabourData
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import org.koin.android.ext.android.inject

class LabourListFragment : Fragment() {

    val viewModel by inject<DashboardViewModel>()

    lateinit var binding: FragmentLabourListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLabourListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_LABOUR_EDIT_DIALOG -> {
                        viewModel.labourDataToEdit.value?.let { labourData ->
                            showLabourEditSheet(labourData)
                        } ?: kotlin.run {
                            viewModel.commonModel.showSnackBar("Unable to edit.")
                        }
                    }
                }
            }
        }
    }


    private fun showLabourEditSheet(labourData: LabourData) {
        InputSheet().show(requireActivity()) {
            title("Update Labour")
            with(InputEditText {
                this.defaultValue(labourData.name)
                required()
                label("Labour Name *")
                hint("Name of the project.")
            })
            with(InputEditText {
                required()
                inputType(InputType.TYPE_CLASS_NUMBER)
                label("Price")
                hint("Default Price of the labour")
                defaultValue("${labourData.price}")
            })
            onNegative { viewModel.commonModel.showSnackBar("Labour update Cancelled") }
            onPositive { result ->
                val name = result.getString("0")
                val price = result.getString("1")
                if (name.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Name is mandatory")
                } else if (price.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Price is mandatory")
                } else {
                    labourData.name = name!!
                    labourData.price = price!!.toIntOrNull() ?: labourData.price
                    updateLabour(labourData)
                }
            }
        }
    }

    private fun updateLabour(labourData: LabourData) {
        viewModel.updateLabour(labourData).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.showSnackBar("Failed to update labour Info.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.showSnackBar("Updated Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_LABOUR_LIST)
                }
            }
        }
    }

}