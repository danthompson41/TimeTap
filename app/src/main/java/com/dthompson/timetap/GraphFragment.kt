package com.dthompson.timetap


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_graph.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GraphFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
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
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
                    xAxis.setDrawGridLines(false)
                    xAxis.setGranularity(1f) // only intervals of 1 day
                    xAxis.setLabelCount(labels.size)
                    xAxis.setValueFormatter(xAxisFormatter)

                    val bardata = BarData(barDataSet)
                    barchart.data =  bardata
                })
            return view;
        }
    }
