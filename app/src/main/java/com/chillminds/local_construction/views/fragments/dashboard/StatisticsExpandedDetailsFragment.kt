package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.FragmentStatisticsExpandedDetailsBinding
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import org.koin.android.ext.android.inject

class StatisticsExpandedDetailsFragment : Fragment() {

    val viewModel by inject<DashboardViewModel>()

    lateinit var binding : FragmentStatisticsExpandedDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsExpandedDetailsBinding.inflate(inflater, container, false)
        binding.dashboardData = viewModel.selectedDashboardStatistics.value
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.expandedDashboardSpinner.apply {
            adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_list_item_1,
                Constants.dashboardExpandedDashboardList
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    viewModel.expandedDashboardSpinnerPosition.postValue(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS -> {
                        exportStatistics()
                    }
                }
            }
        }
    }

    private fun exportStatistics() {
        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromViewList(listOf(binding.recyclerView))
            .setFileName("Construction_Information")
            .setFolderNameOrPath(resources.getString(R.string.app_name))
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse?) {
                    super.onFailure(failureResponse)
                    viewModel.commonModel.showSnackBar("Error - Failed to create a PDF")
                }

                override fun onStartPDFGeneration() {
                    /*When PDF generation begins to start*/
                }

                override fun onFinishPDFGeneration() {
                    /*When PDF generation is finished*/
                }

                override fun showLog(log: String?) {
                    super.showLog(log)
                    Logger.error(
                        "generatePdf", "log: $log"
                    )
                }

                override fun onSuccess(response: SuccessResponse?) {
                    super.onSuccess(response)

                }
            })
    }

}
