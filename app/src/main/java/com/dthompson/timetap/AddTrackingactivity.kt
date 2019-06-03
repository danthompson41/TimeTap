package com.dthompson.timetap


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_trackingactivity.*


/**
 * A simple [Fragment] subclass.
 *
 */
class AddTrackingactivity : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_trackingactivity, container, false)
//        fragment_addEventbutton.setOnClickListener {
//            Log.d("ADDEVENTBUTTON", "Hit the button!")
//            Log.d("ADDEVENTBUTTON", fragment_addEventEditText.text.toString())
//        }
        return view;
    }


}
