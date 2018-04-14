package com.paralect.citiesquiz.utils

import android.app.Activity
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import com.google.android.gms.maps.model.LatLng


/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
object Utils {

    fun getDistance(userCoord: LatLng, gameCoord: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userCoord.latitude, userCoord.longitude,
                gameCoord.latitude, gameCoord.longitude, results)
        return results[0]
    }

    fun hasNetwork(context: Context): Boolean {
        val connMgr = context.getSystemService(Activity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        val hasNetwork = networkInfo != null && networkInfo.isConnected
        return hasNetwork
    }
}