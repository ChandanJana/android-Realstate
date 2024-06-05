package com.realestate.autologout

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.realestate.activity.SplashActivity
import java.util.*


/**
 * Created by Chandan on 2/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
internal class DefaultAppLock(app: Application) : ActivityLifecycleCallbacks {
    val TAG = DefaultAppLock::class.java.simpleName
    private val mCurrentApp: Application
    private val WAIT_TIME = 60 * 60 * 1000.toLong() // 1440 min = 24 hours
    private var waiter: Waiter? = null
    private var mLostFocusDate: Date? = null
    override fun onActivityCreated(p0: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        // for UserInactivity

        // for Screen lock
        if (shouldShowUnlockScreen()) {
            Log.d(TAG, "time over")
            val intent =
                Intent(activity.applicationContext, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.d(TAG, "changing mLostFocus to null")
            mLostFocusDate = null
            activity.applicationContext.startActivity(intent)
        }
    }

    private fun shouldShowUnlockScreen(): Boolean {
        var isvalid = false
        if (mLostFocusDate == null) {
            isvalid = false
        } else {
            Log.d(TAG, "Timeout -&gt;" + timeSinceLocked())
            if (timeSinceLocked() >= WAIT_TIME / 1000) {
                isvalid = true
            } else {
                mLostFocusDate = null
            }
        }
        Log.d(TAG, isvalid.toString())
        return isvalid
    }

    private fun timeSinceLocked(): Long {
        return Math.abs(((Date().time - mLostFocusDate?.time!!) / 1000))
    }

    override fun onActivityPaused(p0: Activity) {
        /*if(waiter!=null) {
                waiter.stopThread();
            }*/
        mLostFocusDate = Date()
    }

    override fun onActivityStopped(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {}
    fun startWaitThread(context: FragmentActivity?) {
        /*if(waiter!=null) {
                waiter.stopThread();
            }*/
        waiter = Waiter(context!!, WAIT_TIME)
        waiter?.startThread()
        waiter?.start()
    }

    fun updateTouch() {
        if (waiter != null) {
            waiter?.touch()
        }
        mLostFocusDate = Date()
    }

    fun setStopTrue() {
        if (waiter != null) {
            waiter?.setStopTrue()
        }
    }

    fun setStopFalse() {
        if (waiter != null) {
            waiter?.setStopFalse()
        }
    }

    init {
        mCurrentApp = app

        //Registering Activity lifecycle callbacks
        mCurrentApp.unregisterActivityLifecycleCallbacks(this)
        mCurrentApp.registerActivityLifecycleCallbacks(this)
    }
}