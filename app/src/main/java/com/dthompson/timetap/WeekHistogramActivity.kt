package com.dthompson.timetap

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import kotlinx.android.synthetic.main.activity_week_histogram.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_current_activity_display.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter


class WeekHistogramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_histogram)
        val barchart = findViewById<HorizontalBarChart>(R.id.barchart)
        var labels = ArrayList<String>();
        var values = ArrayList<BarEntry>();
        Repository.getWeekHistogram().observe(this,
            android.arch.lifecycle.Observer { data ->
                Log.d("WeekHistogram", data.toString())
                var entries = 0f
                data!!.forEach {
                    Log.d("WeekHistogramIt", it.key)
                    labels.add(it.key)
                    values.add(BarEntry(entries, (it.value).toFloat()/1000/60/60))
                    entries++
                }

                val barDataSet = BarDataSet(values, "Hours")

                barchart.setDrawGridBackground(false)


                val xAxisFormatter = IAxisValueFormatter { value, axis ->
                    Log.d("FORMATTER", value.toString())
                    Log.d("FORMATTER", axis.toString())

                    labels[value.toInt()] }

                val xAxis = barchart.getXAxis()
                xAxis.setPosition(XAxisPosition.BOTTOM)
                xAxis.setDrawGridLines(false)
                xAxis.setGranularity(1f) // only intervals of 1 day
                xAxis.setLabelCount(labels.size)
                xAxis.setValueFormatter(xAxisFormatter)

//                val yAxis = barchart.()
//                xAxis.setDrawGridLines(false)


                val bardata = BarData(barDataSet)
                barchart.data =  bardata
      })

    }
}

