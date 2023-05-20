package com.chillminds.local_construction.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun TextView.clearDrawables() {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun TextView.highLightedText(textToHighlight: String, color: Int) {
    val highLightText = textToHighlight.lowercase()
    val tvt = text.toString().lowercase()
    var ofe = tvt.indexOf(highLightText, 0)
    val wordToSpan: Spannable = SpannableString(text)
    var ofs = 0
    while (ofs < tvt.length && ofe != -1) {
        ofe = tvt.indexOf(highLightText, ofs)
        if (ofe == -1) break else {
            // set color here
            wordToSpan.setSpan(
                BackgroundColorSpan(color),
                ofe,
                ofe + highLightText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setText(wordToSpan, TextView.BufferType.SPANNABLE)
        }
        ofs = ofe + 1
    }
}

fun View.showKeyboard() {
    val im: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    im?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

//fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)
  //  ?.hide(WindowInsetsCompat.Type.ime())

fun View.hideKeyBoard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken = this.rootView.windowToken
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}