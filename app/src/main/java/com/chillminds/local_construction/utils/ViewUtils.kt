package com.chillminds.local_construction.utils

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun View.getBitmapFromView(defaultColor: Int? = null): Bitmap {
    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    defaultColor?.let { canvas.drawColor(it) }
    draw(canvas)
    return bitmap
}

fun View.getTheme(): Boolean {
    val nightModeFlags: Int = context.resources
        .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    return when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}