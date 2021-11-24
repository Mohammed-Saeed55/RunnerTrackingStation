package com.example.runningtrackerapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.ui.MainActivity
import com.example.runningtrackerapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context)
    = FusedLocationProviderClient(appContext)


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext appContext: Context)
    = PendingIntent.getActivity(
            appContext,
            0,
            Intent(appContext, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            }, PendingIntent.FLAG_IMMUTABLE
        )!!

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(@ApplicationContext
                                       appContext: Context,
                                       pendingIntent: PendingIntent)
    = NotificationCompat.Builder(appContext, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_run)
        .setContentTitle("Moh-Saeed Tracking App")
        .setContentText("00:00:00:00")
        .setContentIntent(pendingIntent)

}
