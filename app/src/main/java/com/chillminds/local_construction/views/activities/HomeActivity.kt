package com.chillminds.local_construction.views.activities

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.chillminds.local_construction.R
import com.chillminds.local_construction.base.cancelProgress
import com.chillminds.local_construction.base.showProgress
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.ActivityHomeBinding
import com.chillminds.local_construction.models.CommonModel
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.utils.isSdkHigherThan28
import com.chillminds.local_construction.view_models.DashboardViewModel
import org.koin.android.ext.android.inject
import permissions.dispatcher.*

@RuntimePermissions
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    var progressBar: AlertDialog? = null
    val commonModel by inject<CommonModel>()
    val viewModel by inject<DashboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = commonModel
        binding.lifecycleOwner = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        observeFields()
    }

    private fun observeFields() {
        commonModel.actionListener.observe(this) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.REFRESH_MATERIALS_LIST -> getMaterialsData()
                    Actions.REFRESH_STAGE_LIST -> getStagesData()
                    Actions.REFRESH_LABOUR_LIST -> getLabourData()
                    Actions.REFRESH_PROJECT_LIST -> getProjectDetails()
                    Actions.CHECK_PERMISSION_FOR_STORAGE -> exportPdfWithPermissionCheck()
                }
                commonModel.actionListener.postValue("")
            }
        }
        commonModel.progressListener.observe(this) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_PROGRESS_BAR -> showProgress()
                    Actions.CANCEL_PROGRESS_BAR -> cancelProgress()
                }
                commonModel.progressListener.postValue("")
            }
        }
    }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    fun exportPdf() {
        commonModel.actionListener.postValue(Actions.EXPORT_PDF_FROM_DASHBOARD_STATISTICS)
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,)
    fun onPermissionDeniedForStorage(){
        viewModel.commonModel.showSnackBar("Permission Denied")
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForStorage(request: PermissionRequest) {
        //showRationaleDialog(R.string.permission_camera_rationale, request)
        request.proceed()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        commonModel.showSnackBar("Please Enable Permission in settings page.")
    }

    private fun getStagesData() {
        viewModel.getStagesData().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("SUCCESS", response.toString())
                    viewModel.commonModel.stagesData.postValue(response.data?.data)
                }
                ApiCallStatus.ERROR -> {
                    Logger.error("ERROR", response.toString())
                }
                ApiCallStatus.LOADING -> {

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
                    val projectList = response.data?.data
                    viewModel.commonModel.projectList.postValue(projectList)
                    val selectedProject = viewModel.commonModel.selectedProjectDetail.value
                    if (selectedProject == null) {
                        viewModel.commonModel.selectedProjectDetail.postValue(
                            response.data?.data?.get(
                                0
                            )
                        )
                    } else {
                        projectList?.firstOrNull { it.id == selectedProject.id }?.let {
                            viewModel.selectProject(it)
                        }
                    }

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
