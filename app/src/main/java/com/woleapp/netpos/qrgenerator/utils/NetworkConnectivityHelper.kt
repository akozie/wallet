package com.woleapp.netpos.qrgenerator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


class NetworkConnectivityHelper(context: Context) {
        private val networkStatusSubject: BehaviorSubject<Boolean>

        init {
            networkStatusSubject = BehaviorSubject.createDefault(isNetworkAvailable(context))
            observeNetworkConnectivity(context)
        }

        fun observeNetworkStatus(): Observable<Boolean> {
            return networkStatusSubject
        }

        private fun observeNetworkConnectivity(context: Context) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkRequest = NetworkRequest.Builder().build()
            val networkCallback: NetworkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    networkStatusSubject.onNext(true)
                }

                override fun onLost(network: Network) {
                    networkStatusSubject.onNext(false)
                }
            }
            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                } else {
                    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                }
            } else {
                Log.e(TAG, "ConnectivityManager is null")
            }
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                } else {
                    // Legacy way to check network connectivity
                    connectivityManager.activeNetworkInfo != null &&
                            connectivityManager.activeNetworkInfo!!.isConnected
                }
            } else false
        }

        companion object {
            private val TAG = NetworkConnectivityHelper::class.java.simpleName
        }
    }
