package com.realestate.utils

import android.content.Context
import android.net.ConnectivityManager
import com.realestate.app.RealEstateApp

object NetworkUtils {
    val isOnline: Boolean
        get() {
            val connectivityManager =
                RealEstateApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
}