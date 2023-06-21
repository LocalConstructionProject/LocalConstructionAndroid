package com.chillminds.local_construction.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

inline fun <reified T> Context.readRawJson(@RawRes rawResId: Int): T {
    resources.openRawResource(rawResId).bufferedReader().use {
        return Gson().fromJson<T>(it, object : TypeToken<T>() {}.type)
    }
}

fun Activity.isKeyboardOpen(): Boolean {
    val r = Rect()

    val activityRoot = getActivityRoot()
    val visibleThreshold = dip(100)

    activityRoot.getWindowVisibleDisplayFrame(r)

    val heightDiff = activityRoot.rootView.height - r.height()

    return heightDiff > visibleThreshold
}

fun dip(value: Int): Int =
    (value * Resources.getSystem().displayMetrics.density).toInt()


fun Activity.getActivityRoot(): View =
    (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)

fun isSdkHigherThan28() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun Activity.openPdfFile(dest: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW)
    browserIntent.setDataAndType(getUriFromFile(dest), "application/pdf")
    browserIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_ACTIVITY_NO_HISTORY
    //Create Viewer Intent
    val viewerIntent = Intent.createChooser(browserIntent, "Open PDF")
    startActivity(viewerIntent)
}

private fun Activity.getUriFromFile(dest: String): Uri? {
    val file = File(dest)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(file)
    } else {
        FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            file
        )
    }
}

/*
fun Context.checkInternetConnection(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        connectivityManager.activeNetwork ?: return false
    } else {

    }
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_CELLULAR
    ) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || networkCapabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_BLUETOOTH
    )) && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
*/
