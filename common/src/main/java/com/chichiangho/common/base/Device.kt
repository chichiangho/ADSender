package com.chichiangho.common.base

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.chichiangho.common.extentions.appCtx

object Device {
    var appName = ""
        private set
        get() {
            if (field == "")
                init()
            return field
        }

    var appVersion = 0
        private set
        get() {
            if (field == 0)
                init()
            return field
        }

    var appVersionName = ""
        private set
        get() {
            if (field == "")
                init()
            return field
        }

    var deviceType = ""
        private set
        get() {
            if (field == "")
                init()
            return field
        }

    var deviceName = ""
        private set
        get() {
            if (field == "")
                init()
            return field
        }

    var osVersion = ""
        private set
        get() {
            if (field == "")
                init()
            return field
        }

    var screenWidth = 0
        private set
        get() {
            if (field == 0)
                init()
            return field
        }

    var screenHeight = 0
        private set
        get() {
            if (field == 0)
                init()
            return field
        }

    private fun init() {
        try {
            val pm = appCtx.packageManager
            val info = pm.getPackageInfo(appCtx.packageName, 0)
            appVersionName = info.versionName
            appVersion = info.versionCode
            appName = pm.getApplicationLabel(info.applicationInfo) as String

            val metrics = DisplayMetrics()
            val wm = appCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        osVersion = android.os.Build.VERSION.RELEASE
        deviceName = android.os.Build.BRAND
        deviceType = android.os.Build.MODEL
    }
}
