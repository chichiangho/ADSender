package com.chichaingho.library_rx_okhttp

import com.chichiangho.common.extentions.toJson
import io.reactivex.functions.Function

/**
 * Created by chichiangho on 2017/4/28.
 */


class HandleResponse<T> : Function<T, T> {

    @Throws(Exception::class)
    override fun apply(t: T): T {
        if (t is BaseResponse) {//针对post
            val response = t as BaseResponse
            when (response.code) {
                BaseResponse.CODE_SUCCESS -> {
                }
                else -> throw ResponseException(response.toJson())
            }
        }
        return t
    }
}

