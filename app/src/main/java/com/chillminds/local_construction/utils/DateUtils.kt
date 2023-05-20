package com.chillminds.local_construction.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun Long.convertToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd HH:mm", Locale.ENGLISH)
    return format.format(date)
}

fun Long.convertToDateString(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return format.format(date)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.convertToHistoryTime(): String {
    val localDateTime = LocalDateTime.ofEpochSecond(this, 0, OffsetDateTime.now().offset)
    val format = DateTimeFormatter.ofPattern("hh a dd MMMM")

    val dateString = localDateTime.format(format)
    val slicedDate = dateString.split(" ")

    return "${slicedDate[0]} ${slicedDate[1]}</br>${slicedDate[2]} ${slicedDate[3]}"
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.toDate(): LocalDateTime? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    return LocalDateTime.parse(this, formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toStringTime(): String {
    return this.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH))
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toGraphTime(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyyMMddHH", Locale.ENGLISH))
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toLong(): Long {
    val zdt: ZonedDateTime = ZonedDateTime.of(this, ZoneId.systemDefault())
    return zdt.toInstant().toEpochMilli()
}

fun Int.convertSeconds(): String {
    val h = this / 3600
    val m = this % 3600 / 60
    val s = this % 60
    val sh = if (h > 0) "$h h" else ""
    val sm =
        (if (m in 1..9 && h > 0) "0" else "") + if (m > 0) if (h > 0 && s == 0) m.toString() else "$m min" else ""
    val ss =
        if (s == 0 && (h > 0 || m > 0)) "" else (if (s < 10 && (h > 0 || m > 0)) "0" else "") + s.toString() + " " + "sec"
    return sh + (if (h > 0) " " else "") + sm + (if (m > 0) " " else "") + ss
}

fun Double.toPrecision(digits: Int) = String.format("%.${digits}f", this)

fun getDateTime() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LocalDateTime.now().toString()
} else {
    Calendar.getInstance().time.toString()
}

fun Date.toCalendar(): Calendar? = try {
    Calendar.getInstance().apply {
        time = this@toCalendar
    }
} catch (e: Exception) {
    null
}

fun Date.format(): String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(this)
