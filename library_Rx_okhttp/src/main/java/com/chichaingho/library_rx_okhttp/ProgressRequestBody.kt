package com.chichaingho.library_rx_okhttp

/**
 * Created by chichiangho on 2017/5/9.
 */

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class ProgressRequestBody(private val requestBody: RequestBody, private val progressListener: OkHttpClient.ProgressListener?) : RequestBody() {
    private var bufferedSink: BufferedSink? = null

    override fun contentType(): MediaType? = requestBody.contentType()

    @Throws(IOException::class)
    override fun contentLength(): Long = requestBody.contentLength()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink))
        }
        requestBody.writeTo(bufferedSink!!)
        bufferedSink?.flush()

    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            internal var bytesWritten = 0L
            internal var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                if (progressListener != null && bytesWritten != contentLength) {//不触发100%，交由callBack处理done和error，防止Rxjava的onComplete和onError同时触发导致crash
                    progressListener.onProgress((bytesWritten * 100 / contentLength).toInt(), null)
                }
            }
        }
    }
}
