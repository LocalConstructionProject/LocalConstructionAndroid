package com.chillminds.local_construction.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

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
fun String.toDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime =
    LocalDateTime.parse(
        this,
        DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    )

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.countDaysBetween(toDateTime: LocalDateTime) =
    ChronoUnit.DAYS.between(this, toDateTime) + 1

fun Date.countDaysBetween(toDateTime: Date): Long {
    val diffInMilli = abs(time - toDateTime.time)
    return TimeUnit.DAYS.convert(diffInMilli, TimeUnit.MILLISECONDS) + 1
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.countMonthsBetween(toDate: LocalDate): Long =
    ChronoUnit.MONTHS.between(this, toDate) + 1

fun Date.countMonthsBetween(toDateTime: Date): Long {
    val thisCalendar = Calendar.getInstance().apply { time = this@countMonthsBetween }
    val toCalendar = Calendar.getInstance().apply { time = toDateTime }
    val yearsDiff = toCalendar.get(Calendar.YEAR) - thisCalendar.get(Calendar.YEAR)
    val monthsDiff = toCalendar.get(Calendar.MONTH) - thisCalendar.get(Calendar.MONTH)
    return abs(
        yearsDiff * 12 + monthsDiff.toLong()
    ) + 1
}

fun String.toDateBelowOreo(pattern: String = "yyyy-MM-dd"): Date {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.parse(this) ?: Date()
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

fun Date.format(pattern: String = "dd-MM-yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun getLocalDateTimeFormat() = "dd-MM-yyyy HH:mm:ss"
