package com.example.runningtrackerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.db.Run
import com.example.runningtrackerapp.utils.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>(){

    inner class RunViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallBack = object: DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<Run>) = differ.submitList(list)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(run_map_shot)

            val calender: Calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd:MM:yy", Locale.getDefault())
            run_date.text = dateFormat.format(calender.time)
            val avgSpeed: String = "${run.avgSpeedInKMH}-km/h"
            run_avgSpeed.text = avgSpeed
            val distanceInKM: String = "${run.distanceInMeters / 1000f}-km"
            run_distance.text = distanceInKM
            run_time.text = TrackingUtility.formattedStopWatchTimer(run.timeInMillis)
            val caloriesBurned: String = "${run.caloriesBurned}"
            run_calories.text = caloriesBurned
        }
    }

}
