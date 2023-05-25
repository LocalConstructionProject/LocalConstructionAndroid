package com.chillminds.local_construction.base

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.ActivityMainBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.view_models.SplashViewModel
import org.koin.android.ext.android.inject

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
        getMaterialsData()
        getLabourData()
    }

    private fun getLabourData() {
        viewModel.getLabourDetails().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.labourData.postValue(response.data?.data)
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                }
                ApiCallStatus.LOADING -> {

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
                    getProjectDetails()
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                }
                ApiCallStatus.LOADING -> {

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
                    viewModel.commonModel
                        .actionListener.postValue(Actions.GOTO_HOME_PAGE_ACTIVITY)
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                }
                ApiCallStatus.LOADING -> {

                }
            }
        }
    }
}