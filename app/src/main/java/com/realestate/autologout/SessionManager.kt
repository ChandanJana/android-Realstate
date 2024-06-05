package com.realestate.autologout

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.realestate.activity.LoginActivity
import com.realestate.app.RealEstateApp
import com.realestate.storage.SharedPreferenceManager
import com.realestate.utils.Constant


/**
 * Created by Chandan on 2/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class SessionManager(context: Context) {

    companion object{
        fun logoutUserInBackgroundOrForeground(context: FragmentActivity?) {

            SharedPreferenceManager.getInstance(RealEstateApp.context)?.putBoolean(
                SharedPreferenceManager.Key.IS_LOGGEDIN, false
            )
            context?.finishAffinity()
            if (Constant.isAppBackground()) {

                val i = Intent(context, LoginActivity::class.java)
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // Add new Flag to start new Activity
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // Starting Login Activity
                context?.startActivity(i)
            }
        }
    }

}