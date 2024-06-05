package com.realestate.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningAppProcessInfo.*
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.realestate.R
import com.realestate.callback.DialogCallback
import kotlinx.android.synthetic.main.logout_alert_dialog.*
import kotlinx.android.synthetic.main.progress_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Constant {

    companion object {

        private var dialog: Dialog? = null

        @JvmField
        var switchMenu: Boolean = false

        @JvmStatic
        var bottomNavigationBar: BottomNavigationView? = null

        @JvmStatic
        fun convertSecondsToSsOnly(seconds: Long): String {

            val s = seconds % 60

//            val m = seconds / 60 % 60

//            val h = seconds / (60 * 60) % 24

            return String.format("%02d", s)

        }

        @JvmStatic
        fun convertStartStringToTime(dateTime: String, onlyDate: Boolean): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            if (onlyDate) {
                val outputFormat = SimpleDateFormat("dd, MMM yyyy")
                val date: Date = inputFormat.parse(dateTime)
                val time: String = outputFormat.format(date)
                return time
            } else {
                val outputFormat = SimpleDateFormat("dd, MMM yyyy hh:mm aa")
                val date: Date = inputFormat.parse(dateTime)
                val time: String = outputFormat.format(date)
                return time
            }

        }

        @JvmStatic
        fun convertEndStringToTime(dateTime: String, onlyDate: Boolean): String {
            if (dateTime.isNotEmpty()) {
                if (onlyDate) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                    val outputFormat = SimpleDateFormat("dd, MMM yyyy")
                    val date: Date = inputFormat.parse(dateTime)
                    val time: String = outputFormat.format(date)
                    return time
                } else {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                    val outputFormat = SimpleDateFormat("dd, MMM yyyy hh:mm aa")
                    val date: Date = inputFormat.parse(dateTime)
                    val time: String = outputFormat.format(date)
                    return time
                }

            }
            return ""
        }

        @JvmStatic
        fun convertTimeToString(dateTime: String): String {
            if (dateTime.isNotEmpty()) {
                //val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                val date: Date = inputFormat.parse(dateTime)
                val time: String = outputFormat.format(date)
                return time
            }
            return ""
        }


        fun isValidEmail(email: String): Boolean {

            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }

        internal fun showProgress(context: Context, message: String) {
            dialog = Dialog(context)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.progress_dialog)
            dialog?.progress_ok_logout?.setOnClickListener(View.OnClickListener {
                dialog?.dismiss()
            })

            dialog?.progress_cancel_logout?.setOnClickListener(View.OnClickListener {
                dialog?.dismiss()
            })


            //dialog?.txtHeader?.text = "Attention"

            dialog?.progress_load_message?.text = message
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.setCancelable(false)
            dialog?.show()
            /* this.overridePendingTransition(R.anim.slide_left,
            R.anim.slide_left);*/
        }

        internal fun showDialog(
            context: Context,
            title: String,
            message: String,
            listener: DialogCallback
        ) {
            dialog = Dialog(context)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.logout_alert_dialog)
            dialog?.ok_logout?.setOnClickListener(View.OnClickListener {
                dialog?.dismiss()
                listener.onOk(true)
            })

            dialog?.cancel_logout?.setOnClickListener(View.OnClickListener {
                dialog?.dismiss()
                listener.onCancel(true)
            })


            dialog?.txtHeader?.text = title

            dialog?.txtConfirmJoint?.text = message
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.setCancelable(false)
            dialog?.show()
            /* this.overridePendingTransition(R.anim.slide_left,
            R.anim.slide_left);*/
        }

        internal fun hideProgress() {
            if (dialog != null)
                dialog?.dismiss()
        }


        fun convertSecondsToHMmSs(seconds: Long): String {

            val s = seconds % 60

            val m = seconds / 60 % 60

            val h = seconds / (60 * 60) % 24

            return String.format("%02d:%02d:%02d", h, m, s)

        }


        fun convertSecondsToHMmSsdoble(seconds: Double): String {

            val s = seconds % 60.0

            val m = seconds / 60.0 % 60.0

            val h = seconds / (60.0 * 60.0) % 24.0

            return String.format("%02d:%02d:%02d", h.toInt(), m.toInt(), s.toInt())

        }

        fun isAppBackground() : Boolean{
            val appProcessInfo = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(appProcessInfo)
            return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
        }


    }


}