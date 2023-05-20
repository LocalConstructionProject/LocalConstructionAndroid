package com.chillminds.local_construction.models

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.chillminds.local_construction.common.SecurePreference
import org.koin.java.KoinJavaComponent

class CommonModel {

    val sharedPreferences: SecurePreference by KoinJavaComponent.inject(SecurePreference::class.java)
    val message = MutableLiveData<String>()
    val showMessage = MutableLiveData<Boolean>().apply { value = false }
    val actionListener = MutableLiveData<String>()

    fun showSnackBar(message: String,delaySeconds:Int = 3) {
        this.message.postValue(message)
        showMessage.postValue(true)
        Handler(Looper.getMainLooper()).postDelayed({
            showMessage.postValue(false)
        }, delaySeconds * 1000L)
    }

}