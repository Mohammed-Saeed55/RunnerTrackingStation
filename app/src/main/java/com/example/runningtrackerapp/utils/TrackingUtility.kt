package com.example.runningtrackerapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.runningtrackerapp.services.PolyLine
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            EasyPermissions.hasPermissions(context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        else
            EasyPermissions.hasPermissions(context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )


    fun calculatePolyLineLength(myPolyLine: PolyLine): Float {
        var distance = 0f
        for (i in 0..myPolyLine.size -2){
            val pos1: LatLng = myPolyLine[i]
            val pos2: LatLng = myPolyLine[i + 1]
            val result = FloatArray(1)

            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }


    fun formattedStopWatchTimer(ms: Long, includeMillis: Boolean = false): String{
        var milliSeconds: Long = ms

        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)

        if (!includeMillis){
            return "${if (hours < 10)"0" else ""}$hours: " +
                    "${if (minutes < 10)"0" else ""}$minutes: " +
                    "${if (seconds < 10)"0" else ""}$seconds"
        }
        milliSeconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliSeconds /= 10
        return "${if (hours < 10)"0" else ""}$hours: " +
                "${if (minutes < 10)"0" else ""}$minutes: " +
                "${if (seconds < 10)"0" else ""}$seconds: " +
                "${if (milliSeconds < 10)"0" else ""}$milliSeconds"
    }


}
