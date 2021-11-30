package com.example.runningtrackerapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentStatisticsBinding
import com.example.runningtrackerapp.ui.viewmodels.StatisticsViewModel
import com.example.runningtrackerapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var _bind: FragmentStatisticsBinding
    private val bind get() = _bind

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _bind = FragmentStatisticsBinding.inflate(inflater, container, false)

        subscribeToObservers()

        return bind.root
    }

    private fun subscribeToObservers(){
        viewModel.totalRunTime.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalRunTime = TrackingUtility.formattedStopWatchTimer(it)
                bind.totalTimeTv.text = totalRunTime
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km: Float = it /1000f
                val totalDistance: Float = round(km * 10f) / 10f
                val totalDistanceString = "$totalDistance km"
                bind.totalDistanceTv.text = totalDistanceString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed: Float = round(it * 10f) / 10f
                val avgSpeedString = "$avgSpeed km/h"
                bind.averageSpeedTv.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCaloriesString = "$it kcal"
                bind.totalCaloriesTv.text = totalCaloriesString
            }
        })
    }

}
