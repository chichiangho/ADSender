package com.chichaingho.library_rx_okhttp

import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by chichiangho on 2017/3/22.
 */

object OkHttpClientManager {

    internal var client: OkHttpClient? = OkHttpClient()

    fun get(): OkHttpClient {
        if (client == null)
            client = OkHttpClient()
        return client as OkHttpClient
    }

//    fun setCertificates(vararg certificates: InputStream) {
//        try {
//            val certificateFactory = CertificateFactory.getInstance("X.509")
//            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//            keyStore.load(null)
//            for ((index, certificate) in certificates.withIndex()) {
//                val certificateAlias = Integer.toString(index)
//                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate))
//                try {
//                    certificate.close()
//                } catch (e: IOException) {
//                }
//            }
//
//            val sslContext = SSLContext.getInstance("TLS")
//
//            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//
//            trustManagerFactory.init(keyStore)
//            sslContext.init(null,
//                    trustManagerFactory.trustManagers,
//                    SecureRandom()
//            )
//            client = OkHttpClient.Builder().sslSocketFactory(sslContext.socketFactory).build()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
}
