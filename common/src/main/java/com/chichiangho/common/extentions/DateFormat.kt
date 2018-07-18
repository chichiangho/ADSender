package com.chichiangho.common.extentions

import java.text.SimpleDateFormat
import java.util.*

fun String.getTime(format: String = "yyyy-MM-dd HH:mm:ss"): Long = SimpleDateFormat(format, Locale.getDefault()).parse(this).time

fun Long.formatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String = SimpleDateFormat(format, Locale.getDefault()).format(Date(this))

fun Date.format(format: String = "yyyy-MM-dd HH:mm:ss"): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

fun Long.formatDHM(): String {
    val day = this / (24 * 60 * 60 * 1000)
    val hour = this % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)
    val min = this % (60 * 60 * 1000) / (60 * 1000)
    return (if (day == 0L) "" else day.toString() + "天") + (if (hour == 0L) "" else hour.toString() + "小时") + min.toString() + "分钟"
}