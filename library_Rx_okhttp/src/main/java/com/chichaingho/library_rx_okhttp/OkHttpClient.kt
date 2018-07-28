package com.chichaingho.library_rx_okhttp

import com.chichiangho.common.extentions.Logger.Companion.JSON_SPLIT
import com.chichiangho.common.extentions.logD
import com.chichiangho.common.extentions.logJson
import com.chichiangho.common.extentions.toObj
import com.chichiangho.common.extentions.toast
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by chichiangho on 2017/4/1.
 */

object OkHttpClient {
    private val TAG = "OkHttpClient"
    private val requestingURLs = ArrayList<String>(10)

    interface ProgressListener {
        fun onProgress(progress: Int, e: Exception?)
    }

    @Throws(IOException::class)
    operator fun get(url: String): Response {
        logD(TAG, url)
        val request = Request.Builder().url(url).build()
        return OkHttpClientManager.get().newCall(request).execute()
    }

    @Throws(Exception::class)
    fun <T> post(url: String, jsonedparam: String, clz: Class<T>, grepFirst: Boolean = true): T {
        if (grepFirst) {
            synchronized(requestingURLs) {
                for (s in requestingURLs) {
                    if (s == url) {
                        val res = clz.newInstance()
                        (res as BaseResponse).code = BaseResponse.CODE_REPEATED_REQUEST
                        (res as BaseResponse).msg = "IGNORE"
                        return res
                    }
                }
                requestingURLs.add(url)
            }
        }
        try {
            logJson(TAG, url + JSON_SPLIT + jsonedparam)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonedparam)
            val request = Request.Builder().url(url).post(body).build()
            val response = OkHttpClientManager.get().newCall(request).execute()

            return if (response.isSuccessful) {
                val result = response.body()!!.string()
                logJson(TAG, url + JSON_SPLIT + result)
                result.toObj(clz) ?: let {
                    val res = clz.newInstance()
                    (res as BaseResponse).msg = "接口返回非正常的response"
                    res
                }
            } else {
                logD(TAG, "error " + response.code() + ":" + response.message())
                clz.newInstance()
            }
        } finally {
            if (grepFirst) {
                synchronized(requestingURLs) {
                    requestingURLs.remove(url)
                }
            }
        }
    }

    @Throws(Exception::class)
    @JvmOverloads
    fun downLoad(url: String, path: String, listener: ProgressListener? = null) {
        logD(TAG, "downloading $url to $path")
        val request = Request.Builder().url(url).build()
        OkHttpClientManager.get().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener?.onProgress(-1, e)
                logD(TAG, "download $url failure")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var `is`: InputStream? = null
                var fos: FileOutputStream? = null
                val buf = ByteArray(2048)
                var len: Int

                try {
                    `is` = response.body()?.byteStream()
                    val total = response.body()?.contentLength() ?: 1
                    val file = File(path)
                    if (!file.exists())
                        file.createNewFile()
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (true) {
                        len = `is`!!.read(buf)
                        if (len == -1)
                            break
                        fos.write(buf, 0, len)
                        if (listener != null) {
                            sum += len.toLong()
                            val progress = (sum * 1.0f / total * 100).toInt()
                            listener.onProgress(progress, null)
                        }
                    }
                    fos.flush()
                    listener?.onProgress(100, null)
                    logD(TAG, "download $url success")
                } catch (e: Exception) {
                    listener?.onProgress(-1, e)
                    logD(TAG, "download $url failure")
                } finally {
                    `is`?.close()
                    fos?.close()
                }
            }
        })
    }

    @Throws(Exception::class)
    @JvmOverloads
    fun upLoad(url: String, path: String, listener: ProgressListener? = null) {
        logD(TAG, "uploading $path to $url")
        val file = File(path)
        if (!file.exists())
            throw Exception("$path not exists!")

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        val fileName = path.substring(path.lastIndexOf("/", path.length))
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"$fileName\""),
                RequestBody.create(MediaType.parse("application/octet-stream"), file))

        if (listener == null)
            builder.addFormDataPart("file", file.name, builder.build())
        else
            builder.addFormDataPart("file", file.name, ProgressRequestBody(builder.build(), listener))

        val requestBody = builder.build()

        val request = Request.Builder().url(url).post(requestBody).build()

        OkHttpClientManager.get().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener?.onProgress(-1, e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resulr = response.body()?.string()
                    val code = JSONObject(resulr).getInt("code")
                    val msg = JSONObject(resulr).getString("msg")
                    if (code == 1000) {
                        listener?.onProgress(100, null)
                    } else {
                        listener?.onProgress(-1, Exception(msg))
                    }
                } else {
                    listener?.onProgress(-1, Exception(response.message()))
                }
            }
        })
    }
}
