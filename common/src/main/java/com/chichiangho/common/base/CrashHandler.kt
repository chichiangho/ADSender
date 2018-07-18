package com.chichiangho.common.base

import com.chichiangho.common.extentions.logE
import com.chichiangho.common.extentions.logToFile
import java.sql.Timestamp

/**
 * Created by chichiangho on 2017/4/18.
 */

class CrashHandler : Thread.UncaughtExceptionHandler {

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会回调该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        val builder = StringBuilder("\nBrand: " + Device.deviceName
                + ", Model: " + Device.deviceType + ", OS: " + "Android "
                + Device.osVersion + ", Version: " + Device.appVersionName + "\n")
        builder.append(ex.toString() + "\n")
        builder.append(ex.localizedMessage + "\n")
        val stack = ex.stackTrace
        for (element in stack) {
            builder.append(element.toString() + "\n")
        }
        logE("CrashHandler", "系统异常退出" + builder.toString())
        var throwable: Throwable? = ex.cause
        while (null != throwable) {
            val currentStack = throwable.stackTrace
            for (element in currentStack) {
                builder.append(element.toString() + "\n")
            }
            throwable = throwable.cause
        }
        logToFile(Timestamp(System.currentTimeMillis()).toString(), builder,
                "com_demo_crash.log")

//        val log = builder.toString()

        //        RxHttpDataProvider.upLoadCrashLog(log, new RxHttpObserver<BaseResponse>() {
        //            @Override
        //            public void onStart() {
        //
        //            }
        //
        //            @Override
        //            public void onSuccess(BaseResponse response) {
        //
        //            }
        //
        //            @Override
        //            public void onFailed() {
        //
        //            }
        //
        //            @Override
        //            public void onComplete() {
        android.os.Process.killProcess(android.os.Process.myPid())
        //            }
        //        });
    }
}
