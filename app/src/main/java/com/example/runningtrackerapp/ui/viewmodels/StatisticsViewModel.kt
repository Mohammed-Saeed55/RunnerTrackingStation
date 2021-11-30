package com.example.runningtrackerapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.runningtrackerapp.db.Run
import com.example.runningtrackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class StatisticsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    val totalRunTime: LiveData<Long> = mainRepository.getTotalTimeInMillis()
    val totalDistance: LiveData<Long> = mainRepository.getTotalDistanceInMeters()
    val totalCaloriesBurned: LiveData<Long> = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed: LiveData<Long> = mainRepository.getTotalAvgSpeedInKMH()
    val runsSortedByDate: LiveData<List<Run>> = mainRepository.getAllRunsSortedByDate()

}
