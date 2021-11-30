package com.example.runningtrackerapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningtrackerapp.db.RunDAO
import com.example.runningtrackerapp.db.RunningDatabase
import com.example.runningtrackerapp.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtrackerapp.utils.Constants.KEY_NAME
import com.example.runningtrackerapp.utils.Constants.KEY_WEIGHT
import com.example.runningtrackerapp.utils.Constants.RUNNING_DATABASE_NAME
import com.example.runningtrackerapp.utils.Constants.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext appContext: Context): RunningDatabase =
        Room.databaseBuilder(appContext, RunningDatabase::class.java, RUNNING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase): RunDAO = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideUserName(sharedPref: SharedPreferences): String =
        sharedPref.getString(KEY_NAME, "")?: ""

    @Singleton
    @Provides
    fun provideUserWeight(sharedPref: SharedPreferences): Float =
        sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences): Boolean =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)


}
