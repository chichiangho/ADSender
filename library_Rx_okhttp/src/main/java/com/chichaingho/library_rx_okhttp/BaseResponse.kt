package com.chichaingho.library_rx_okhttp

import java.io.Serializable


class BaseResponse : Serializable {
    var code = 0
    var msg = ""

    companion object {
        val CODE_SUCCESS = 1000 // 返回数据成功
        val CODE_TOKEN_TIMEOUT = 1007 // 用户Token过期，需要重新登录
        val CODE_REPEATED_REQUEST = 1008
    }
}
