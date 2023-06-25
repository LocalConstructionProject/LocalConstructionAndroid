package com.chillminds.local_construction.views.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.FragmentStageTypeWiseStatisticsListBinding
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.getDateTime
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import org.koin.android.ext.android.inject

class StageTypeWiseStatisticsListFragment : Fragment() {

    var position: Int = 0

    lateinit var binding: FragmentStageTypeWiseStatisticsListBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStageTypeWiseStatisticsListBinding.inflate(inflater, container, false)
        position = arguments?.getInt("position") ?: 0
        binding.viewModel = viewModel
        binding.position = position
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS -> {
                        if (position == (viewModel.projectDashboardPosition.value ?: -1)) {
                            exportStatistics()
                        }
                    }
                }
            }
        }
    }

    private fun exportStatistics() {
        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromViewList(listOf(binding.headingView, binding.recyclerView))
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
                    Logger.error(
                        "generatePdf", "log: $log"
                    )
                }

                override fun onSuccess(response: SuccessResponse?) {
                    super.onSuccess(response)

                }
            })
    }

    companion object {
        fun newInstance(position: Int?): StageTypeWiseStatisticsListFragment {
            val args = Bundle()
            args.putInt("position", position ?: 0)
            val f = StageTypeWiseStatisticsListFragment()
            f.arguments = args
            return f
        }
    }
}