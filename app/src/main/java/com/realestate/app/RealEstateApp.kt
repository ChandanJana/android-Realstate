package com.realestate.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.realestate.autologout.ApplockManager


/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class RealEstateApp : MultiDexApplication(), LifecycleObserver {
    companion object {
        lateinit var sInstance: RealEstateApp
        lateinit var context: Context
        var isBackground: Boolean = false

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        Log.d("MyApp", "App in background")
        isBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        Log.d("MyApp", "App in foreground")
        isBackground = false
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sInstance = this
        //ApplockManager.instance?.enableDefaultAppLockIfAvailable(this);
        //ApplockManager.instance?.startWaitThread(sInstance)
    }

    fun touch() {
        ApplockManager.instance?.updateTouch()
    }

    fun setStopTrue() {
        ApplockManager.instance?.setStopTrue()
    }

    fun setStopFalse() {
        //ApplockManager.instance?.setStopFalse()
        //ApplockManager.instance?.startWaitThread(sInstance)
    }
}