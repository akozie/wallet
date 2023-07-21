package com.woleapp.netpos.qrgenerator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import io.reactivex.Observable

class Connectivity(val context: Context) {

    // Get the ConnectivityManager instance
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    // Get the active network information
    @RequiresApi(Build.VERSION_CODES.M)
    val network: Network? = connectivityManager.activeNetwork

    // Get the network capabilities for the active network
    @RequiresApi(Build.VERSION_CODES.M)
    val networkCapabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)

    // Check if the network has a transport with low bandwidth
    @RequiresApi(Build.VERSION_CODES.M)
    val isLowConnectivity: Boolean = networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            && networkCapabilities.linkDownstreamBandwidthKbps < 5 // Adjust the threshold as per your requirements

    // Get the active network information
    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

    // Check if there is network connectivity
   // val isConnected: Observable<Boolean> = networkInfo != null && networkInfo.isConnected

}