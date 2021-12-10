package com.example.runningtrackerapp.utils

import android.content.Context
import com.example.runningtrackerapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(private val runs: List<Run>, c: Context, layoutId: Int): MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null) return
        else {
            val curRunId = e.x.toInt()
            val run = runs[curRunId]
            val calender: Calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd:MM:yy", Locale.getDefault())
            marker_data.text = dateFormat.format(calender.time)
            val avgSpeed: String = "${run.avgSpeedInKMH}-km/h"
            marker_avg_speed.text = avgSpeed
            val distanceInKM: String = "${run.distanceInMeters / 1000f}-km"
            marker_distance.text = distanceInKM
            marker_duration.text = TrackingUtility.formattedStopWatchTimer(run.timeInMillis)
            val caloriesBurned: String = "${run.caloriesBurned}"
            marker_calories_burned.text = caloriesBurned
        }
    }
}
