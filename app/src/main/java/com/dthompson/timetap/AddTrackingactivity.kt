package com.dthompson.timetap


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_trackingactivity.*


/**
 * A simple [Fragment] subclass.
 *
 */
class AddTrackingactivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_trackingactivity, container, false)
        val button = view.findViewById<Button>(R.id.fragment_addEventbutton)
        val name = view.findViewById<EditText>(R.id.fragment_addEventEditText)
        button.setOnClickListener {
            Log.d("ADDEVENTBUTTON", "Hit the button!")
            Log.d("ADDEVENTBUTTON", name.text.toString())
            Repository.AddActivity(name.text.toString())
            (activity as MainActivity).notify_activity_add()
        }
        return view;
    }
}
