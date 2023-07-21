package com.woleapp.netpos.qrgenerator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.SyncStateContract
import android.telephony.TelephonyManager
import androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED
import androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED
import androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED


//object SlowInternet {
//    /**
//     * Get the network info
//     *
//     * @param context
//     * @return
//     */
//    fun getNetworkInfo(context: Context): NetworkInfo? {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return cm.activeNetworkInfo
//    }
//
//    /**
//     * Check if there is any connectivity
//     *
//     * @param context
//     * @return
//     */
//    fun isConnected(context: Context?): Boolean {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return info != null && info.isConnected
//    }
//
//    fun isConnectedWifi(context: Context?): Boolean {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
//    }
//
//    fun isConnectedMobile(context: Context?): Boolean {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
//    }
//
//    /**
//     * Check if there is fast connectivity
//     *
//     * @param context
//     * @return
//     */
//    fun isConnectedFast(context: Context?): Boolean {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return info != null && info.isConnected && Connectivity.isConnectionFast(
//            info.type,
//            info.subtype
//        )
//    }
//
//    /**
//     * Check if the connection is fast
//     *
//     * @param type
//     * @param subType
//     * @return
//     */
//    fun isConnectionFast(type: Int, subType: Int): Boolean {
//        return if (type == ConnectivityManager.TYPE_WIFI) {
//            true
//        } else if (type == ConnectivityManager.TYPE_MOBILE) {
//            when (subType) {
//                TelephonyManager.NETWORK_TYPE_1xRTT -> true // ~ 50-100 kbps
//                TelephonyManager.NETWORK_TYPE_CDMA -> true // ~ 14-64 kbps
//                TelephonyManager.NETWORK_TYPE_EDGE -> true // ~ 50-100 kbps
//                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
//                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
//                TelephonyManager.NETWORK_TYPE_GPRS -> true // ~ 100 kbps
//                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
//                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
//                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
//                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
//                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
//                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
//                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
//                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
//                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
//                TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
//                else -> false
//            }
//        } else {
//            false
//        }
//    }
//
//    fun getConnectionStrength(context: Context?): String {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return if (info != null && info.isConnected) {
//            Connectivity.getInternetStrength(info.type, info.subtype, context)
//        } else {
//            "Not Connected"
//        }
//    }
//
//    fun getInternetStrength(type: Int, subType: Int, context: Context): String {
//        return if (type == ConnectivityManager.TYPE_WIFI) {
//            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//            val numberOfLevels = 5
//            val wifiInfo = wifiManager.connectionInfo
//            val level = WifiManager.calculateSignalLevel(wifiInfo.rssi, numberOfLevels)
//            "" + level /* + " out of 5"*/
//        } else if (type == ConnectivityManager.TYPE_MOBILE) {
//            when (subType) {
//                TelephonyManager.NETWORK_TYPE_CDMA -> "" + 1 //Poor
//                TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS -> "" + 3 //Fair
//                TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_LTE -> "" + 5 //Good
//                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "" + 0 //No Connection
//                else -> "" + 0
//            }
//        } else {
//            "Not Connected"
//        }
//    }
//
//    /***
//     * Get Device Connection Status
//     * @param context Calling Context.
//     * @return Connectivity signal status value which is based on Network Info
//     */
//    fun getConnectionStatus(context: Context?): String {
//        val info: NetworkInfo = Connectivity.getNetworkInfo(context)
//        return if (info == null || !info.isConnected) {
//            Constants.ConnectionSignalStatus.NO_CONNECTIVITY
//        } else if (Connectivity.getInternetStatus(info.type, info.subtype, context) == 3
//            && Utils.getBatteryPercentageDouble(context) > 20
//        ) {
//            Constants.ConnectionSignalStatus.GOOD_STRENGTH
//        } else if (Connectivity.getInternetStatus(info.type, info.subtype, context) >= 2
//            && Utils.getBatteryPercentageDouble(context) > 20
//        ) {
//            Constants.ConnectionSignalStatus.FAIR_STRENGTH
//        } else if (Connectivity.getInternetStatus(info.type, info.subtype, context) >= 2
//            && Utils.getBatteryPercentageDouble(context) <= 20
//        ) {
//            SyncStateContract.Constants.ConnectionSignalStatus.BATTERY_LOW
//        } else {
//            Constants.ConnectionSignalStatus.POOR_STRENGTH
//        }
//    }
//
//    fun getInternetStatus(type: Int, subType: Int, context: Context): Int {
//        return if (type == ConnectivityManager.TYPE_WIFI) {
//            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//            val numberOfLevels = 5
//            val wifiInfo = wifiManager.connectionInfo
//            val level = WifiManager.calculateSignalLevel(wifiInfo.rssi, numberOfLevels)
//            if (level < 2) {
//                2 //Fair
//            } else {
//                3 //Good
//            }
//        } else if (type == ConnectivityManager.TYPE_MOBILE) {
//            when (subType) {
//                TelephonyManager.NETWORK_TYPE_CDMA -> 1 //Poor
//                TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS -> 1 //Fair
//                TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_LTE -> 3 //Good
//                TelephonyManager.NETWORK_TYPE_UNKNOWN -> 0 //No Connection
//                else -> 0
//            }
//        } else {
//            0
//        }
//    }
//
//    /**
//     * Return the availability of cellular data access in background.
//     *
//     * @param context Application or Activity context.
//     *
//     * @return Availability of cellular data access in background.
//     */
//    fun isBackgroundDataAccessAvailable(context: Context): Boolean {
//        var isBackgroundDataAccessAvailable = true
//        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (connMgr != null) {
//            // Checks if the device is on a metered network
//            if (connMgr.isActiveNetworkMetered) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                    // Checks user’s Data Saver settings.
//                    when (connMgr.restrictBackgroundStatus) {
//                        RESTRICT_BACKGROUND_STATUS_DISABLED ->                             // Data Saver is disabled. Since the device is connected to a
//                            // metered network, the app should use less data wherever possible.
//                            isBackgroundDataAccessAvailable = true
//                        RESTRICT_BACKGROUND_STATUS_WHITELISTED ->                             // The app is whitelisted. Wherever possible,
//                            // the app should use less data in the foreground and background.
//                            isBackgroundDataAccessAvailable = true
//                        RESTRICT_BACKGROUND_STATUS_ENABLED ->                             // Background data usage is blocked for this app. Wherever possible,
//                            // the app should also use less data in the foreground.
//                            isBackgroundDataAccessAvailable = false
//                    }
//                } else {
//                    val state = connMgr.activeNetworkInfo!!.state
//                    isBackgroundDataAccessAvailable = state != NetworkInfo.State.DISCONNECTED
//                }
//            } else {
//                // The device is not on a metered network.
//                // Use data as required to perform syncs, downloads, and updates.
//                isBackgroundDataAccessAvailable = true
//            }
//        } else {
//            isBackgroundDataAccessAvailable = true
//        }
//        return isBackgroundDataAccessAvailable
//    }
//}