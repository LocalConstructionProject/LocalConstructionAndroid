package com.chillminds.holy_bible.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.RequiresApi

fun CharSequence?.isNullOrEmptyOrBlank(): Boolean =
    this == null || isBlank() || isEmpty() || trim().isEmpty()

@RequiresApi(Build.VERSION_CODES.N)
fun String.getSpannedText(): Spanned? = Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)

fun String?.isContainAnswer() = this?.matches(Regex("[A-Da-d]")) ?: false

fun String?.getDefaultName() = this ?: listOf("Jabez",
    "Job",
    "Noah",
    "Adam",
    "Thomas",
    "Ruban",
    "Peter",
    "Levi",
    "Salomon",
    "Ruth").random()

fun String.toCamelCase(): String =
    split('_').joinToString(" ") {
        it.beginWithUpperCase()
    }

fun String.beginWithUpperCase(): String = when (this.length) {
    0 -> ""
    1 -> lowercase()
    else -> this[0].uppercase() + substring(1).lowercase()
}

fun <E> Collection<E>.arrayToString(): String =
    toString().replace("[", "").replace("]", "")
