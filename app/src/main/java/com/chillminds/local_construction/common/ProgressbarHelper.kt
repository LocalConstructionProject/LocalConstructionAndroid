package com.chillminds.local_construction.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.R
import com.chillminds.local_construction.base.lazyFast

class ProgressbarHelper(private val context: Context, var message: CharSequence?) {

    private val dialogView: View by lazyFast {
        LayoutInflater.from(context).inflate(
            R.layout.progressbar_layout,
            null
        )
    }

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(dialogView)


    private val progressBar: ProgressBar by lazyFast {
        dialogView.findViewById<ProgressBar>(R.id.progressBar)
    }

    private val title: TextView by lazyFast {
        dialogView.findViewById<TextView>(R.id.message)
    }

    private var dialog: AlertDialog? = null

    var cancelable: Boolean = false

    init {
        if (message.isNullOrEmptyOrBlank()) {
            message = "Please Wait..!"
        }
        this.title.text = message
    }

    fun create(): AlertDialog {

        dialog = builder
            .setCancelable(cancelable)
            .create()

        return dialog!!
    }

}
