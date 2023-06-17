package com.chillminds.local_construction.common

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.chillminds.local_construction.R
import com.chillminds.local_construction.base.GlideApp
import com.chillminds.local_construction.base.lazyFast
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank

class ProgressbarHelper(private val context: Context, var message: CharSequence?) {

    private val dialogView: View by lazyFast {
        LayoutInflater.from(context).inflate(
            R.layout.progressbar_layout,
            null
        )
    }

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(dialogView)


    private val progressBar: AppCompatImageView by lazyFast {
        dialogView.findViewById(R.id.progressBar)
    }

    private val title: TextView by lazyFast {
        dialogView.findViewById(R.id.message)
    }

    private var dialog: AlertDialog? = null

    var cancelable: Boolean = false

    init {
        if (message.isNullOrEmptyOrBlank()) {
            message = "Please Wait..!"
        }
        this.title.text = message
        GlideApp.with(progressBar).asGif().load(R.drawable.loading_animation).into(progressBar)
    }

    fun create(): AlertDialog {

        dialog = builder
            .setCancelable(cancelable)
            .create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog!!
    }

}
