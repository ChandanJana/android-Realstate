package com.realestate.autologout

import android.util.Log
import androidx.fragment.app.FragmentActivity

/**
 * Created by Chandan on 2/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class Waiter(context: FragmentActivity, period: Long) : Thread() {
    private var lastUsed: Long = 0
    private var period: Long
    private var stop: Boolean = false
    private val mContext: FragmentActivity
    override fun run() {
        var idle: Long = 0
        touch()
        do {
            idle = System.currentTimeMillis() - lastUsed
            if (idle > period) {
                idle = 0
                // Perform Your desired Function like Logout or expire the session for the app.
                stopThread()
            }
        } while (!stop)
        Log.d(TAG, "Finishing Waiter thread")
    }

    @Synchronized
    fun touch() {
        lastUsed = System.currentTimeMillis()
    }

    @Synchronized
    fun setStopTrue() {
        stop = true
    }

    @Synchronized
    fun setStopFalse() {
        stop = false
    }

    @Synchronized
    fun forceInterrupt() {
        interrupt()
    }

    @Synchronized
    fun setPeriod(period: Long) {
        this.period = period
    }

    @Synchronized
    fun stopThread() {
        stop = true
        SessionManager.logoutUserInBackgroundOrForeground(mContext)
    }

    @Synchronized
    fun startThread() {
        stop = false
    }

    companion object {
        private val TAG = Waiter::class.java.name
    }

    init {
        this.period = period
        this.stop = false
        this.mContext = context
    }
}