package com.dthompson.timetap


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_current_activity_display.*
import java.util.*
import java.util.Calendar
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CurrentActivityDisplayFragment : Fragment() {

    var currentActivityStart : Date = Date()
    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_current_activity_display, container, false)
        Repository.getCurrentActivity().observe(this,
            android.arch.lifecycle.Observer { data ->
                Log.d("CurrentActivity", data.toString())
                currentActivityName.text = data!!
            })
        Repository.getCurrentActivityStartTime().observe(this,
            android.arch.lifecycle.Observer { data ->
                Log.d("CurrentActivityStart", data.toString())
                currentActivityStart = data!!

            })
        handler.postDelayed( {
            update();
        }, 1000);
        return view
    }

    fun update() {
        val diff = Calendar.getInstance().time.time - currentActivityStart.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) - (60 * minutes)
        if (Repository.getCurrentActivity().value == "") {
            currentActivityDuration.text = ""
        } else {
            currentActivityDuration.text = minutes.toString() + " Minutes, " + seconds.toString() + " Seconds";
        }
        handler.postDelayed({
            update();
        }, 1000)
    }
}
