package com.example.runningtrackerapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningtrackerapp.db.Run
import com.example.runningtrackerapp.repositories.MainRepository
import com.example.runningtrackerapp.utils.SortedTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    val runs = MediatorLiveData<List<Run>>()


    val runsSortedByDate: LiveData<List<Run>> = mainRepository.getAllRunsSortedByDate()
    val runsSortedByDistance: LiveData<List<Run>> = mainRepository.getAllRunsSortedByDistanceInMeters()
    val runsSortedByCalories: LiveData<List<Run>> = mainRepository.getAllRunsSortedByCaloriesBurned()
    val runsSortedByTime: LiveData<List<Run>> = mainRepository.getAllRunsSortedByTimeInMillis()
    val runsSortedBySpeed: LiveData<List<Run>> = mainRepository.getAllRunsSortedByAvgSpeedInKMH()

    var sortType = SortedTypes.DATE  // The Default Sort Type

    init {
        runs.addSource(runsSortedByDate) { resultObserve ->
            if (sortType == SortedTypes.DATE) resultObserve?.let { runs.value = it } }

        runs.addSource(runsSortedByDistance) { resultObserve ->
            if (sortType == SortedTypes.DISTANCE) resultObserve?.let { runs.value = it } }

        runs.addSource(runsSortedByCalories) { resultObserve ->
            if (sortType == SortedTypes.CALORIES_BURNED) resultObserve?.let { runs.value = it } }

        runs.addSource(runsSortedByTime) { resultObserve ->
            if (sortType == SortedTypes.RUNNING_TIME) resultObserve?.let { runs.value = it } }

        runs.addSource(runsSortedBySpeed) { resultObserve ->
            if (sortType == SortedTypes.AVG_SPEED) resultObserve?.let { runs.value = it } }
    }

    fun sortRuns(sortingType: SortedTypes) =
        when(sortingType){
            SortedTypes.DATE -> runsSortedByDate.value?.let { runs.value = it }
            SortedTypes.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
            SortedTypes.CALORIES_BURNED -> runsSortedByCalories.value?.let { runs.value = it }
            SortedTypes.RUNNING_TIME -> runsSortedByTime.value?.let { runs.value = it }
            SortedTypes.AVG_SPEED -> runsSortedBySpeed.value?.let { runs.value = it }
        }.also {
            this.sortType = sortingType
        }


    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}
