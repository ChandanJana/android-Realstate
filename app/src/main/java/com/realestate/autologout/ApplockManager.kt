package com.realestate.autologout

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity


/**
 * Created by Chandan on 2/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class ApplockManager {
    private var currentAppLocker: DefaultAppLock? = null
    fun enableDefaultAppLockIfAvailable(currentApp: Application?) {
        currentAppLocker = DefaultAppLock(currentApp!!)
    }

    fun startWaitThread(context: FragmentActivity?) {
        currentAppLocker?.startWaitThread(context)
    }

    fun updateTouch() {
        currentAppLocker?.updateTouch()
    }

    fun setStopTrue() {
        currentAppLocker?.setStopTrue()
    }

    fun setStopFalse() {
        currentAppLocker?.setStopFalse()
    }

    companion object {
        var instance: ApplockManager? = null
            get() {
                if (field == null) {
                    field = ApplockManager()
                }
                return field
            }
            private set
    }
}