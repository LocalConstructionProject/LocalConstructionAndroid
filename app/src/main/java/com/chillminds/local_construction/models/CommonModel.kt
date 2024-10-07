package com.chillminds.local_construction.models

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.SecurePreference
import com.chillminds.local_construction.repositories.remote.dto.LabourData
import com.chillminds.local_construction.repositories.remote.dto.MaterialData
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail
import com.chillminds.local_construction.repositories.remote.dto.RentalInformation
import com.chillminds.local_construction.repositories.remote.dto.RentalProduct
import com.chillminds.local_construction.repositories.remote.dto.StageDetail
import org.koin.java.KoinJavaComponent

class CommonModel {

    val sharedPreferences: SecurePreference by KoinJavaComponent.inject(SecurePreference::class.java)
    val message = MutableLiveData<String>()
    val showMessage = MutableLiveData<Boolean>().apply { value = false }
    val actionListener = MutableLiveData<String>()
    val progressListener = MutableLiveData<String>()
    val materialData = MutableLiveData<List<MaterialData>>()
    val splashMessage = MutableLiveData<String>().apply { value = "Connecting with server..!" }
    val labourData = MutableLiveData<List<LabourData>>()
    val stagesData = MutableLiveData<List<StageDetail>>()
    val projectList = MutableLiveData<List<ProjectDetail>>()
    val rentalProductList = MutableLiveData<List<RentalProduct>>()
    val rentalDataInformationList = MutableLiveData<List<RentalInformation>>()
    val selectedProjectDetail = MutableLiveData<ProjectDetail?>().apply { value = null }
    val selectedRentalProduct = MutableLiveData<RentalProduct?>().apply { value = null }
    val selectedRentalInformation = MutableLiveData<RentalInformation?>().apply { value = null }
    val projectToDelete = MutableLiveData<ProjectDetail?>().apply { value = null }
    val rentalProductToDelete = MutableLiveData<RentalProduct?>().apply { value = null }
    val rentalInfoToDelete = MutableLiveData<RentalInformation?>().apply { value = null }
    val dashboardProjectDetail = MutableLiveData<ProjectDetail?>().apply { value = null }

    fun showSnackBar(message: String, delaySeconds: Int = 3) {
        this.message.postValue(message)
        showMessage.postValue(true)
        Handler(Looper.getMainLooper()).postDelayed({
            showMessage.postValue(false)
        }, delaySeconds * 1000L)
    }

    fun goBack(view: View) {
        view.findNavController().navigateUp()
    }

    fun showProgress() {
        progressListener.postValue(Actions.SHOW_PROGRESS_BAR)
    }

    fun cancelProgress() {
        progressListener.postValue(Actions.CANCEL_PROGRESS_BAR)
    }

}