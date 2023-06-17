package com.chillminds.local_construction.base

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.ActivityMainBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.view_models.SplashViewModel
import com.maxkeppeler.sheets.info.InfoSheet
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    var progressBar: AlertDialog? = null
    private lateinit var binding: ActivityMainBinding
    val viewModel by inject<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        observeFields()
    }

    private fun observeFields() {
        callAPI()
        viewModel.successAPICount.observe(this) {
            if (it == 4) {
                viewModel.commonModel
                    .actionListener.postValue(Actions.GOTO_HOME_PAGE_ACTIVITY)
            } else {
                validateFailureCount(it)
            }
        }
        viewModel.failureAPICount.observe(this) {
            if (it != 0) {
                validateFailureCount(viewModel.successAPICount.value?:0)
            }
        }
    }

    private fun validateFailureCount(successCount: Int) {
        val failureCount = viewModel.failureAPICount.value?:0
        if(failureCount.plus(successCount) == 4) {
            InfoSheet().show(this) {
                title("Oh No")
                this.content("We are facing some problem. Can you check your network connectivity and try again.!")
                onNegative("Exit") {
                    finishAffinity()
                    exitProcess(0)
                }
                onPositive("Retry") {
                    callAPI()
                }
            }
        }
    }

    private fun callAPI() {
        viewModel.resetCount()
        getMaterialsData()
        getLabourData()
        getProjectDetails()
        getStagesData()
    }

    private fun getStagesData() {
        viewModel.getStagesData().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("SUCCESS", response.toString())
                    viewModel.commonModel.stagesData.postValue(response.data?.data)
                    viewModel.updateSuccessCount()
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                    viewModel.updateErrorCount()
                }
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.splashMessage.postValue(Constants.PREPARING_CONSTRUCTION_STAGES)
                }
            }
        }
    }

    private fun getLabourData() {
        viewModel.getLabourDetails().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("Labour Data", response.data.toString())
                    viewModel.commonModel.labourData.postValue(response.data?.data)
                    viewModel.updateSuccessCount()
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                    viewModel.updateErrorCount()
                }
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.splashMessage.postValue(Constants.PREPARING_LABOUR_INFORMATION)
                }
            }
        }
    }

    private fun getMaterialsData() {
        viewModel.getAllMaterials().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("Material Details", response.data.toString())
                    viewModel.commonModel.materialData.postValue(response.data?.data)
                    viewModel.updateSuccessCount()
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                    viewModel.updateErrorCount()
                }
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.splashMessage.postValue(Constants.PREPARING_MATERIAL_INFORMATION)
                }
            }
        }
    }

    private fun getProjectDetails() {
        viewModel.getAllProjects().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("Project Details", response.data.toString())
                    viewModel.commonModel.projectList.postValue(response.data?.data)
                    viewModel.commonModel.selectedProjectDetail.postValue(response.data?.data?.firstOrNull())
                    viewModel.updateSuccessCount()
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                    viewModel.updateErrorCount()
                }
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.splashMessage.postValue(Constants.PREPARING_PROJECT_INFORMATION)
                }
            }
        }
    }
}