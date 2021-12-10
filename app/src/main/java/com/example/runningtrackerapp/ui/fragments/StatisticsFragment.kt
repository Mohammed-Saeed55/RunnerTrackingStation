package com.example.runningtrackerapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentStatisticsBinding
import com.example.runningtrackerapp.ui.viewmodels.StatisticsViewModel
import com.example.runningtrackerapp.utils.CustomMarkerView
import com.example.runningtrackerapp.utils.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
        setupBarChart()

        return bind.root
    }



    private fun setupBarChart(){
        bind.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(true)
        }
        bind.barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(true)
        }
        bind.barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(true)
        }
        bind.barChart.apply {
            description.text = "AVG Speed Over Time"
            legend.isEnabled = false
        }
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
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAVGSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAVGSpeed, "AVG Speed Over Time").apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                bind.barChart.data = BarData(barDataSet)
                bind.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                bind.barChart.invalidate()
            }
        })
    }
}
