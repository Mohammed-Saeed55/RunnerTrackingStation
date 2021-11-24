package com.example.runningtrackerapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtrackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtrackerapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runningtrackerapp.utils.Constants.FASTEST_LOCATION_UPDATE
import com.example.runningtrackerapp.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_ID
import com.example.runningtrackerapp.utils.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningtrackerapp.utils.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>


@Suppress("RedundantSamConstructor")
@AndroidEntryPoint
class TrackingServices: LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    private var isFirstRun: Boolean = true
    private var terminateCurrentRun: Boolean = false
    private val timeRunInSeconds: MutableLiveData<Long> = MutableLiveData<Long>()

    private var isTimeEnabled: Boolean = false
    private var lapTime: Long = 0L
    private var totalRunTime: Long = 0L
    private var startedTime: Long = 0L
    private var lastSecondTimeStamp: Long = 0L


    companion object {
        val timeRunInMillis: MutableLiveData<Long> = MutableLiveData<Long>()
        val isTracking: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val pathPoints: MutableLiveData<PolyLines> = MutableLiveData<PolyLines>()
    }


    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        currentNotificationBuilder = baseNotificationBuilder
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForGroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Started Or Resumed Service")
                        startForGroundService()
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pausedService()
                    Timber.d("Paused Service")
                }
                ACTION_STOP_SERVICE -> {
                    terminateCurrentRun()
                    Timber.d("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }



    private fun pausedService() {
        isTimeEnabled = false
        isTracking.postValue(false)
    }



    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking)"Pause" else "Resume"
        val pendingIntent = if (isTracking){
            val pauseIntent = Intent(this, TrackingServices::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingServices::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_IMMUTABLE)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())  //Clear Notification
        }

        if (!terminateCurrentRun){
            currentNotificationBuilder = baseNotificationBuilder.addAction(   //Create the Notification
                R.drawable.ic_pause_circle, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())  //Show the Notification
        }
    }


/*
    @SuppressLint("RestrictedApi", "UnspecifiedImmutableFlag")
    private fun updateNotificationTrackingState(isTracking: Boolean) {

        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = PendingIntent.getService(this,
            if (isTracking) 1 else 2,
            Intent(this, TrackingServices::class.java).apply {
                action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
            }, FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        currentNotificationBuilder.mActions.clear()

        if (serviceKilled(TrackingServices::class.java))
        { currentNotificationBuilder = baseNotificationBuilder.addAction(
            R.drawable.ic_pause_circle,
            notificationActionText,
            pendingIntent
        )

            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())

        }
    }


    private fun serviceKilled(mClass: Class<TrackingServices>): Boolean{
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE))
            if (mClass.name.equals(service.service.className)) return true
        return false
    }
*/


    private fun terminateCurrentRun(){
        terminateCurrentRun = true
        isFirstRun = true
        pausedService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking)
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request: LocationRequest = LocationRequest.create().apply{
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE
                    priority = PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }else fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
    }



    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result!!)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("Location: ${location.latitude} ${location.longitude}")
                    }
                }
            }
        }
    }



    private fun startTimer(){
        addEmptyPolyLine()
        isTracking.postValue(true)
        startedTime = System.currentTimeMillis()
        isTimeEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - startedTime //Time Between Started & Now
                timeRunInMillis.postValue(totalRunTime + lapTime) //Post New LapTimer

                if (timeRunInMillis.value!! >= lastSecondTimeStamp) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            totalRunTime += lapTime
        }
    }

    private fun addPathPoint(location: Location?){
        location?.let{
            val pos= LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }


    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun startForGroundService(){   //Notification Panel
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(notificationManager)
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        //Notification Timer Start on Observer
        timeRunInSeconds.observe(this, Observer {
            if (!terminateCurrentRun){
                val notification = currentNotificationBuilder.setContentText(
                    TrackingUtility.formattedStopWatchTimer(it *1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}
