package com.chichiangho.common.extentions

import android.os.Environment
import android.util.Log
import com.changhong.common.BuildConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by chichiangho on 2017/10/18.
 */

val log = if (BuildConfig.DEBUG) Logger() else null

fun logV(msg: String) {
    log?.v(Logger.DEFAULT_TAG, msg)
}

fun logD(msg: String) {
    log?.d(Logger.DEFAULT_TAG, msg)
}

fun logI(msg: String) {
    log?.i(Logger.DEFAULT_TAG, msg)
}

fun logW(msg: String) {
    log?.w(Logger.DEFAULT_TAG, msg)
}

fun logE(msg: String) {
    log?.e(Logger.DEFAULT_TAG, msg)
}

fun logJson(msg: String) {
    log?.json(Logger.DEFAULT_TAG, msg)
}

fun logV(tag: String, msg: String) {
    log?.v(tag, msg)
}

fun logD(tag: String, msg: String) {
    log?.d(tag, msg)
}

fun logI(tag: String, msg: String) {
    log?.i(tag, msg)
}

fun logW(tag: String, msg: String) {
    log?.w(tag, msg)
}

fun logE(tag: String, msg: String) {
    log?.e(tag, msg)
}

fun logJson(tag: String, msg: String) {
    log?.json(tag, msg)
}

fun logToFile(time: String, msg: Any?, fileName: String) {
    log?.logToFile(time, msg, fileName)
}

class Logger {
    companion object {
        val DEFAULT_TAG = "Logger"
        val JSON_SPLIT = "( ゜- ゜)つロ"
        private val SINGLE_DIVIDER = "┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈"
        val TOP_BORDER = '┌' + SINGLE_DIVIDER + SINGLE_DIVIDER
        val BOTTOM_BORDER = '└' + SINGLE_DIVIDER + SINGLE_DIVIDER
        val MIDDLE_BORDER = '├' + SINGLE_DIVIDER + SINGLE_DIVIDER
        val JSON_INDENT = 2
        val MAX_STACK_LINE_COUNT = 5// 打印调用栈的行數,MAX_STACK_LINE_COUNT行
    }

    internal fun e(TAG: String, msg: String) {
        if (msg.isNotBlank()) {
            val s = getMethodNames()
            Log.e(TAG, String.format(s, msg))
        }
    }

    fun w(TAG: String, msg: String) {
        if (msg.isNotBlank()) {
            val s = getMethodNames()
            Log.w(TAG, String.format(s, msg))
        }
    }

    fun i(TAG: String, msg: String) {
        if (msg.isNotBlank()) {
            val s = getMethodNames()
            Log.i(TAG, String.format(s, msg))
        }
    }

    fun d(TAG: String, msg: String) {
        if (msg.isNotBlank()) {
            val s = getMethodNames()
            Log.d(TAG, String.format(s, msg))
        }
    }

    fun v(TAG: String, msg: String) {
        if (msg.isNotBlank()) {
            val s = getMethodNames()
            Log.v(TAG, String.format(s, msg))
        }
    }

    fun json(TAG: String, jsonStr: String) {
        if (jsonStr.isBlank()) {
            d(TAG, "Empty/Null json content")
            return
        }

        try {
            val jsonSplit = jsonStr.split(JSON_SPLIT).toTypedArray()
            var pre = ""
            var json: String
            if (jsonSplit.size < 2) {
                json = jsonSplit[0]
            } else {
                pre = jsonSplit[0]
                json = jsonSplit[1]
            }

            when {
                json.startsWith("{") -> {
                    val jsonObject = JSONObject(json)
                    json = (if (pre.isEmpty()) "" else pre + "\n") + jsonObject.toString(JSON_INDENT)
                    formatLogJson(TAG, json)
                }
                json.startsWith("[") -> {
                    val jsonArray = JSONArray(json)
                    json = (if (pre.isEmpty()) "" else pre + "\n") + jsonArray.toString(JSON_INDENT)
                    formatLogJson(TAG, json)
                }
                else -> e(TAG, "Invalid Json")
            }
        } catch (e: JSONException) {
            e(TAG, "Invalid Json")
        }
    }

    private fun formatLogJson(TAG: String, jsons: String) {
        val json = jsons.replace("${'\\'}", "").replace("\n".toRegex(), "\n┊ ")
        val lineLength = 2560
        var start = 0
        var end: Int
        for (i in 0..99) {
            if (start >= json.length)
                break

            val s = if (start + lineLength >= json.length) ""
            else json.substring(start + lineLength, if (start + lineLength + lineLength > json.length) json.length else start + lineLength + lineLength)

            end = start + lineLength + s.indexOfFirst { it -> it == '\n' }

            if (end >= json.length)
                end = json.length
            if (i == 0)
                Log.d(TAG, String.format(getMethodNames(false), json.substring(start, end)))
            else
                Log.d(TAG, json.substring(start, end))
            start = end
        }
        Log.d(TAG, BOTTOM_BORDER)
    }

    fun logToFile(time: String, msg: Any?, fileName: String) {
        if (null == msg) {
            return
        }
        writeToSDCard(fileName, "[" + time + "] " + msg.toString() + "\n")
    }

    private fun getMethodNames(withLastLine: Boolean = true): String {
        val sElements = Thread.currentThread().stackTrace
        var stackOffset = getStackOffset(sElements)
        stackOffset += 2 //Logger类和Log.kt2个方法占用了2个栈
        val builder = StringBuilder()
        builder.append(TOP_BORDER)
                .append("\r\n") // 添加当前线程名
                .append("┊ " + "Thread: " + Thread.currentThread().name)
                .append("\r\n")
                .append(MIDDLE_BORDER)
                .append("\r\n") // 添加类名、方法名、行数
        for (i in 0..if (sElements.size - stackOffset > MAX_STACK_LINE_COUNT - 1) MAX_STACK_LINE_COUNT - 1 else sElements.size - stackOffset) {
            builder.append("┊ ")
                    .append(sElements[stackOffset + i].className)
                    .append(".")
                    .append(sElements[stackOffset + i].methodName)
                    .append(" ")
                    .append(" (")
                    .append(sElements[stackOffset + i].fileName)
                    .append(":").append(sElements[stackOffset + i].lineNumber)
                    .append(")")
                    .append("\r\n")
        }
        builder.append(MIDDLE_BORDER)
                .append("\r\n") // 添加打印的日志信息
                .append("┊ ")
                .append("%s")
                .append("\r\n")
        if (withLastLine)
            builder.append(BOTTOM_BORDER)
                    .append("\r\n")
        return builder.toString()
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = 3
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != Logger::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun writeToSDCard(fileName: String, text: String) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var fileOutputStream: FileOutputStream? = null
            val fileInputStream: FileInputStream? = null
            try {
                val file = File(Environment.getExternalStorageDirectory(), fileName)
                fileOutputStream = FileOutputStream(file, true)
                fileOutputStream.write(text.toByteArray())
            } catch (e: Exception) {

            } finally {
                try {
                    fileOutputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    fileInputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}