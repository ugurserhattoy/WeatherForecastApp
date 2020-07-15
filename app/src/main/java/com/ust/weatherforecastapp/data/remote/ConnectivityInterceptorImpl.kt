package com.ust.weatherforecastapp.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ust.weatherforecastapp.interior.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response


class ConnectivityInterceptorImpl(
    context: Context //needed to check if user has internet connection
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline())
            throw NoConnectivityException()

        return chain.proceed(chain.request())

    }

    private fun  isOnline(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
//        val networkInfo = ConnectivityManager.NetworkCallback()
//        return networkInfo.onAvailable() != null
        val mNetworkInfo = connectivityManager.activeNetwork
        return if (mNetworkInfo != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(mNetworkInfo)
            (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            }else {
                false
            }
//   For API level < 23 situation
//        val connectivityManager =
//            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (connectivityManager != null) {
//            if (Build.VERSION.SDK_INT < 23) {
//                val mNetworkInfo = connectivityManager.activeNetworkInfo
//                if (mNetworkInfo != null) {
//                    return mNetworkInfo.isConnected &&
//                            (mNetworkInfo.type == ConnectivityManager.TYPE_WIFI ||
//                                    mNetworkInfo.type == ConnectivityManager.TYPE_MOBILE ||
//                                            mNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET)
//                }
//            } else {
//                val n = connectivityManager.activeNetwork
//                if (n != null) {
//                    val networkCapabilities = connectivityManager.getNetworkCapabilities(n)
//                    return networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
//                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
//                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
//                }
//            }
//        }
//        return false
    }
}