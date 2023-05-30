package com.chillminds.local_construction.utils

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import java.io.ByteArrayOutputStream
import java.io.IOException


fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun ImageView.setColorTint(color: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(color)
    )
}

fun Int.toColorValue() = ColorStateList.valueOf(this)

fun Int.toHexColor() = String.format("#%06X", 0xFFFFFF and this)

fun ImageView.setSize(w: Int, h: Int) {

    val layoutParams: ViewGroup.LayoutParams = layoutParams.apply {
        width = w
        height = h
    }

    setLayoutParams(layoutParams)
}

fun ImageView.getBitmapFromAsset(strName: String): Bitmap? {
    val assetManager = context.assets
    return try {
        BitmapFactory.decodeStream(assetManager.open(strName))
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun Drawable.drawableToByteArray(): ByteArray {
    return drawableToStream().toByteArray()
}

fun Drawable.drawableToBitmap(): Bitmap? {
    return (this as BitmapDrawable).bitmap
}

fun Drawable.drawableToStream(): ByteArrayOutputStream {
    val bitmap = drawableToBitmap()
    val stream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream
}


