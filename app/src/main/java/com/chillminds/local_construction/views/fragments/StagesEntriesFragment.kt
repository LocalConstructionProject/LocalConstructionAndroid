package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.FragmentStagesEntriesBinding
import com.chillminds.local_construction.repositories.remote.dto.ProjectStageDetail
import com.chillminds.local_construction.utils.dateConversion
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import org.koin.android.ext.android.inject

class StagesEntriesFragment() : Fragment() {

    var position: Int = 0

    lateinit var binding: FragmentStagesEntriesBinding

    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStagesEntriesBinding.inflate(inflater, container, false)
        position = arguments?.getInt("position") ?: 0

        binding.data = getProjectStageDetail()
        binding.viewModel = viewModel
        binding.lifeCycle = this
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun getProjectStageDetail(): ProjectStageDetail? {
        val stages = viewModel.commonModel.selectedProjectDetail.value?.stages
        return if (stages.isNullOrEmpty() || stages.size <= position) {
            null
        } else {
            stages[position]
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS -> {
                        if(position == (viewModel.projectStagesTabPosition.value ?: -1)) {
                            exportStatistics()
                        }
                    }
                }
            }
        }
    }

    private fun exportStatistics() {
        val stageDetail = getProjectStageDetail()
        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromViewList(listOf(binding.recyclerView))
            .setFileName("${stageDetail?.name}-${stageDetail?.startedDate?.dateConversion()}")
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
        fun newInstance(position: Int?): StagesEntriesFragment {
            val args = Bundle()
            args.putInt("position", position ?: 0)
            val f = StagesEntriesFragment()
            f.arguments = args
            return f
        }
    }

}