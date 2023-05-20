package com.chillminds.local_construction.base

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.chillminds.local_construction.common.ProgressbarHelper

fun MainActivity.showProgress(message: String) {
    progressBar = showProgressBar(message).apply { show() }
}

fun MainActivity.cancelProgress() {
    progressBar?.dismiss()
}

fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun Activity.showProgressBar(
    message: CharSequence? = null
): AlertDialog {
    return ProgressbarHelper(
        this,
        message
    ).create()
}