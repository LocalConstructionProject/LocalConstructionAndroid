package com.chillminds.local_construction.views.fragments.settings

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentMaterialListBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.MaterialData
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import org.koin.android.ext.android.inject

class MaterialListFragment : Fragment() {

    val viewModel by inject<DashboardViewModel>()
    lateinit var binding: FragmentMaterialListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMaterialListBinding.inflate(inflater, container, false)
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
                    Actions.SHOW_MATERIAL_EDIT_DIALOG -> {
                        viewModel.materialDataToEdit.value?.let { materialData ->
                            showMaterialEditSheet(materialData)
                        } ?: kotlin.run {
                            viewModel.commonModel.showSnackBar("Unable to edit.")
                        }
                    }
                }
             }
        }
    }

    private fun showMaterialEditSheet(materialData: MaterialData) {
        InputSheet().show(requireActivity()) {
            title("Update Material")
            with(InputEditText {
                required()
                label("Material Name ")
                hint("Name of the project.")
                defaultValue(materialData.name)
            })
            with(InputEditText {
                required()
                inputType(InputType.TYPE_CLASS_NUMBER)
                label("Price")
                hint("Price of the material")
                defaultValue("${materialData.amount}")
            })
            onNegative { viewModel.commonModel.showSnackBar("Material update Cancelled") }
            onPositive { result ->
                val name = result.getString("0")
                val price = result.getString("1")
                if (name.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Name is mandatory")
                } else if (price.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Price is mandatory")
                } else {
                    materialData.name = name!!
                    materialData.amount = price!!.toIntOrNull() ?: materialData.amount
                    updateMaterial(materialData)
                }
            }
        }
    }

    private fun updateMaterial(materialData: MaterialData) {
        viewModel.updateMaterial(materialData).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.showSnackBar("Failed to update material Info.")
                }
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.showSnackBar("Updated Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_MATERIALS_LIST)
                }
            }
        }
    }

}