package com.realestate.utils

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.realestate.R

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class AppNavigator {
    companion object {

        /*@JvmStatic
        fun navigate(activity: FragmentActivity, moduleType: String) {
            val intent = getModuleType(activity, moduleType)
            if (intent != null) {
                activity.startActivity(intent)
            }
        }*/

        /*@JvmStatic
        fun navigate(activity: FragmentActivity, moduleType: String, isFinish: Boolean) {
            val intent = getModuleType(activity, moduleType)
            if (intent != null) {
                activity.startActivity(intent)
                if (isFinish)
                    activity.finish()
            }
        }*/

        /*@JvmStatic
        fun navigate(
            activity: FragmentActivity,
            moduleType: String,
            isFinish: Boolean,
            isClearAll: Boolean
        ) {
            val intent = getModuleType(activity, moduleType)
            if (intent != null) {
                if (isClearAll) intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
                if (isFinish)
                    activity.finish()
            }
        }*/

        /*@JvmStatic
        fun navigate(
            activity: FragmentActivity,
            moduleType: String,
            isFinish: Boolean,
            bundle: Bundle
        ) {
            val intent = getModuleType(activity, moduleType)
            intent?.putExtras(bundle)
            if (intent != null) {
                activity.startActivity(intent)
                if (isFinish)
                    activity.finish()
            }
        }*/

        /*@JvmStatic
        fun navigate(activity: FragmentActivity, moduleType: String, bundle: Bundle) {
            val intent = getModuleType(activity, moduleType)
            intent?.putExtras(bundle)
            if (intent != null) {
                activity.startActivity(intent)
            }
        }*/

        /*private fun getModuleType(activity: FragmentActivity, moduleType: String): Intent? =
            when (moduleType) {
                ModuleType.SPLASH -> Intent(activity, SplashActivity::class.java)
                ModuleType.HOME -> Intent(activity, DashbordActivity::class.java)
                ModuleType.LOGIN -> Intent(activity, InviteActivity::class.java)
                else -> null
            }*/

        @JvmStatic
        fun navigate(activity: FragmentActivity, container: Int, fragmentTag: String) {
            FragmentProvider.getFragment(fragmentTag)?.let {
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(container, it, fragmentTag)
                    .commit()
            }
        }

        @JvmStatic
        fun navigate(
            activity: FragmentActivity,
            container: Int,
            fragmentTag: String,
            bundle: Bundle
        ) {
            FragmentProvider.getFragment(fragmentTag)?.arguments = bundle
            FragmentProvider.getFragment(fragmentTag)?.let {
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, it, fragmentTag)
                    .commit()
            }
        }

        @JvmStatic
        @Synchronized
        fun navigate(
            activity: FragmentActivity,
            container: Int,
            fragmentTag: String,
            isAddToBackStack: Boolean
        ) {
            if (isAddToBackStack) {
                FragmentProvider.getFragment(fragmentTag)?.let {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(container, it, fragmentTag)
                        .addToBackStack(fragmentTag)
                        .commit()
                }

            } else {
                FragmentProvider.getFragment(fragmentTag)?.let {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(container, it, fragmentTag)
                        .commit()
                }

            }
        }

        @JvmStatic
        fun navigate(
            activity: FragmentActivity,
            container: Int,
            fragmentTag: String,
            isAddToBackStack: Boolean,
            bundle: Bundle
        ) {
            var fragment = FragmentProvider.getFragment(fragmentTag)
            fragment?.arguments = bundle
            if (isAddToBackStack) {
                fragment.let {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(container, it!!, fragmentTag)
                        .addToBackStack(fragmentTag)
                        .commit()
                }

            } else {
                fragment.let {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(container, it!!, fragmentTag)
                        .commit()
                }

            }
        }

    }
}