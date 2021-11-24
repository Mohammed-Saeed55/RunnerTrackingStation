//package com.example.runningtrackerapp.di
//
//import android.app.ActivityManager
//import android.content.Context
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService
//import com.example.runningtrackerapp.services.TrackingServices
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ActivityComponent
//import dagger.hilt.android.qualifiers.ActivityContext
//import dagger.hilt.android.scopes.ActivityScoped
//import dagger.hilt.android.scopes.ServiceScoped
//
//
//@Module
//@InstallIn(ActivityComponent::class)
//object activityModule {
//
//    @ActivityScoped
//    @Provides
//    private fun provideServiceKilled(@ActivityContext
//                                     appContext: AppCompatActivity,
//                                     mClass: Class<TrackingServices>): Boolean{
//        val manager: ActivityManager = getSystemService(appContext.ACTIVITY_SERVICE) as ActivityManager
//
//        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)){
//            if (mClass.name.equals(service.service.className)) return true
//        }
//        return false
//    }
//
//}
