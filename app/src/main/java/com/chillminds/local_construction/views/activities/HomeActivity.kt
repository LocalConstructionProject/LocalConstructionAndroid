package com.chillminds.local_construction.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Logger
import com.chillminds.local_construction.databinding.ActivityHomeBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.view_models.SplashViewModel
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    val viewModel by inject<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        observeFields()
    }

    private fun observeFields() {
        getProjectDetails()
    }

    private fun getProjectDetails() {
        viewModel.getAllProjects().observe(this) { response ->
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    Logger.error("Project Details", response.data.toString())
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