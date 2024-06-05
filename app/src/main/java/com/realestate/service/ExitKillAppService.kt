package com.realestate.service

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.IBinder
import com.realestate.app.RealEstateApp
import com.realestate.storage.SharedPreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ExitKillAppService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY;
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        println("onAppKilled called")
        super.onTaskRemoved(rootIntent)

        try {
            /*SharedPreferenceManager.getInstance(RealEstateApp.context)?.putBoolean(
                SharedPreferenceManager.Key.IS_LOGGEDIN, false
            )*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
        // this.stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}