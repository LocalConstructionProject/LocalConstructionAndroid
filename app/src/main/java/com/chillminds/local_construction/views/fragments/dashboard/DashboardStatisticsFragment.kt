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
import com.chillminds.local_construction.databinding.FragmentDashboardStatisticsBinding
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.getDateTime
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import org.koin.android.ext.android.inject

class DashboardStatisticsFragment : Fragment() {

    lateinit var binding: FragmentDashboardStatisticsBinding
    val viewModel by inject<DashboardViewModel>()
    private var xmlToPDFLifecycleObserver: PdfGenerator.XmlToPDFLifecycleObserver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardStatisticsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        xmlToPDFLifecycleObserver = PdfGenerator.XmlToPDFLifecycleObserver(requireActivity())
        lifecycle.addObserver(xmlToPDFLifecycleObserver!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dashboardSpinner.apply {
            adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_list_item_1,
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

        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS -> {
                        viewModel.commonModel.showSnackBar("Coming Soon..!")
                        exportStatistics()
                    }
                }
            }
        }
    }

    private fun exportStatistics() {
        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewIDSource()
            .fromViewID(
                requireActivity(), R.id.view2,
                R.id.nameTextView,
                R.id.textView,
                R.id.textView4,
                R.id.view,
                R.id.recyclerView
            )
            .setFileName("PROJECT_DOCUMENT_" + getDateTime().dateConversion())
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
                    viewModel.commonModel.showSnackBar("Info - $log")
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