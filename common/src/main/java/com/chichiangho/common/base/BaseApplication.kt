package com.chichiangho.common.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import java.util.*
import kotlin.collections.ArrayList

open class BaseApplication : Application() {
    lateinit var refWatcher: RefWatcher
    private val activityStack = ArrayList<Activity>()
    val lifeCycleListenerMap = HashMap<Activity, HashMap<String, ArrayList<() -> Unit>>>()

    fun getTopActivity(): Activity? {
        for (i in activityStack.size - 1 downTo 0) {
            val top = activityStack[i]
            if (!top.isDestroyed)
                return top
        }
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        refWatcher = LeakCanary.install(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                lifeCycleListenerMap[activity]?.get("onPaused")?.forEach { it() }
            }

            override fun onActivityResumed(activity: Activity) {
                lifeCycleListenerMap[activity]?.get("onResumed")?.forEach { it() }
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityStack.remove(activity)
                lifeCycleListenerMap[activity]?.get("onDestroyed")?.forEach { it() }
                lifeCycleListenerMap.remove(activity)
                refWatcher.watch(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                activityStack.add(activity)
            }
        })
    }

    companion object {
        lateinit var instance: BaseApplication
    }
}
