package com.chichaingho.library_rx_okhttp

import com.chichiangho.common.extentions.toObj
import com.chichiangho.common.extentions.toast
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created by chichiangho on 2017/4/27.
 */

abstract class RxHttpObserver<T> : Observer<T> {
    override fun onSubscribe(d: Disposable) {
        onStart()
    }

    override fun onNext(value: T) {
        onSuccess(value)//错误码解析在HandleResponse，然后在onError处理
    }

    override fun onError(e: Throwable) {
        when (e) {
            is ResponseException -> {
                e.message?.let {
                    when {
                        it.startsWith("{") -> {
                            val res = it.toObj(BaseResponse::class.java)
                            when (res.code) {
//                                BaseResponse.CODE_NET_ERROR -> toast("网络错误，请稍后再试...")
                                BaseResponse.CODE_TOKEN_TIMEOUT -> {
//                                    toast(res.message)
//                                    PreferanceUtil.removeMarkCarInfo()
//                                    PreferanceUtil.removeUserInfo()
//                                    val intent = Intent(BaseApplication.getInstance(), LoginActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                    BaseApplication.getInstance().startActivity(intent)
                                }
                                else -> toast(res.msg)
                            }
                        }
                        else -> toast(it)
                    }
                }
            }
            is SocketTimeoutException ->
                toast("网络超时，请重试")
            is ConnectException ->
                toast("网络连接失败，请检查网络")
            else ->
                toast(e.message)
        }

        onFailed()
        onComplete()
    }

    protected open fun onStart() {}

    protected abstract fun onSuccess(response: T)

    protected open fun onFailed() {}

    override fun onComplete() {}
}
