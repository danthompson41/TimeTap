package com.dthompson.timetap

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext
import java.util.*
import java.util.zip.Inflater

//import android.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TimeActivityListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TimeActivityListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TimeActivityListFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var mainActivity: MainActivity = activity as MainActivity
        val view = inflater.inflate(R.layout.fragment_time_activity_list, container, false)
        val list = view.findViewById<ListView>(R.id.listview)
        var thingstoadd: MutableList<String>
        Repository.getActivitiesNames().observe(this,
            android.arch.lifecycle.Observer { data ->
                Log.d("ObserverFromFragment", data.toString())
                thingstoadd = data!!
                val adapter = MyListAdapter(inflater, thingstoadd, mainActivity)
                list.adapter = adapter;
            })
        return view
    }
}

class MyListAdapter(private var inflater: LayoutInflater, private var items: MutableList<String>, private var mainActivity: MainActivity) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var button: Button? = null
        var mainActivity: MainActivity? = null;

        init {
            this.button = row?.findViewById(R.id.singletimeactivitybutton)
            button?.setOnClickListener {
                mainActivity?.logCalendar();
                Repository.LogEvent(button!!.text.toString())
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.single_time_activity, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var item = items[position]
        viewHolder.button?.text = item;
        viewHolder.mainActivity = mainActivity

        return view as View
    }

    override fun getItem(i: Int): String {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}