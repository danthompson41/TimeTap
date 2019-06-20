package com.dthompson.timetap

import android.content.ContentValues.TAG
import android.util.Log
import java.util.*
//class Single
class Stats {
    fun ComputeHistogram(list: List<SingleEvent>) : HashMap<String, Long> {
        val histogram = HashMap<String, Long>()
        var previousEvent : SingleEvent? = null
        list.forEach { s ->
            Log.d("ComputeHistogram", s.toString());
            if (previousEvent != null) {
                val duration = s.start_time.time - previousEvent!!.start_time.time
                if (histogram.containsKey(previousEvent!!.name)) {
                    val prev_value: Long = histogram.get(previousEvent!!.name)!!
                    histogram.set(previousEvent!!.name, prev_value!! + duration)
                } else {
                    histogram.put(previousEvent!!.name, duration)
                }
            }
            previousEvent = s;
        }
        Log.d("Histogram", histogram.toString())
        return histogram
    }
    fun ComputeTimeSeries(list: List<SingleEvent>) {
        list.forEach { s ->
            Log.d("ComputeTimeSeries", s.toString());
        }
    }
}