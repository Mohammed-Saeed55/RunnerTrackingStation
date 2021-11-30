package com.example.runningtrackerapp.repositories

import com.example.runningtrackerapp.db.Run
import com.example.runningtrackerapp.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDAO: RunDAO) {

    suspend fun insertRun(run: Run) = runDAO.insertRun(run)
    suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)



    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()
    fun getAllRunsSortedByAvgSpeedInKMH() = runDAO.getAllRunsSortedByAvgSpeedInKMH()
    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()
    fun getAllRunsSortedByDistanceInMeters() = runDAO.getAllRunsSortedByDistanceInMeters()
    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()



    fun getTotalAvgSpeedInKMH() = runDAO.getTotalAvgSpeedInKMH()
    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()
    fun getTotalDistanceInMeters() = runDAO.getTotalDistanceInMeters()
    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
}
