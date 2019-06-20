package com.dthompson.timetap

import android.app.PendingIntent.getActivity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentValues.TAG
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import android.content.Intent
import android.provider.CalendarContract.Events
import android.provider.CalendarContract
import android.support.v4.content.ContextCompat.startActivity
import kotlin.collections.HashMap


class SingleEvent {
    var name: String = "";
    var start_time: Date = Date();

}

class dataModel() {
    val email: String? = null;
    val name: String? = null;
//    val events = ArrayList<HashMap<String, kotlin.Any>>();
    val events = mutableListOf<SingleEvent>();

    val uid: String? = null;
    val activities = ArrayList<HashMap<String, Int>>();
    val currentActivity = SingleEvent();
}

object Repository {
    private var userData: dataModel? = null;
    private val currentUser = HashMap<String, kotlin.Any>()
    private val db = FirebaseFirestore.getInstance()
    private val activityNames = MutableLiveData<MutableList<String>>()
    private var activitiesNameList = mutableListOf<String>()
    private val currentActivity = MutableLiveData<String>()
    private val currentActivityStartTime = MutableLiveData<Date>()
    private var EventsListDay =  mutableListOf<SingleEvent>()
    private var EventsListWeek = mutableListOf<SingleEvent>()
    private val WeekHistogram = MutableLiveData<HashMap<String,Long>>()
    private val DayTimeSeries = MutableLiveData<HashMap<String,Long>>()


    public fun LoadUser(user: FirebaseUser) {
        if (user == null) {
            Log.d("LOADUSERFAIL", "Loading failed")
        } else {
            val displayName = user.displayName
            if (displayName != null) {
                currentUser.put("name", displayName)
            }
            val email = user.email
            if (email != null) {
                currentUser.put("email", email)
            }
            val uid = user.uid
            if (uid != null) {
                currentUser.put("uid", uid)
            }
            Log.d("USERSTUFF", user.displayName);
            Log.d("USERSTUFF", user.email);
            Log.d("USERSTUFF", user.photoUrl.toString());
            Log.d("USERSTUFF", user.isEmailVerified.toString());
            Log.d("USERSTUFF", user.uid);
            userId = currentUser["uid"].toString();
            val s: String = currentUser["uid"].toString();
            db.collection("users")
                .document(s).set(currentUser, SetOptions.merge())

            db.collection("users").document(s).addSnapshotListener { newuserdata, e-> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                }
            }
                val newuser = newuserdata!!.data;
                Log.d(TAG, newuser.toString());
                userData = newuserdata!!.toObject(dataModel::class.java)
                Log.d(TAG, userData?.events.toString());
                Log.d(TAG, userData?.activities.toString());
                activitiesNameList = mutableListOf<String>();
                for (i in userData?.activities!!) {
                    for ((k,v) in i) {
                        activitiesNameList.add(k)
                    }
                }
                activityNames.value = activitiesNameList;
                currentActivity.value = userData!!.currentActivity.name;
                currentActivityStartTime.value = userData!!.currentActivity.start_time;
                val cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                val day = cal.time
                cal.add(Calendar.DATE, -6);
                val week = cal.time;
                Log.d(TAG, day.toString())
                userData!!.events.forEach { s -> Log.d("EVENTLIST", s.name) }
                EventsListDay = userData!!.events.filter{s -> s.start_time > day}.toMutableList()
                EventsListDay.forEach {s -> Log.d("EVENTLISTDay", s.name)}
                EventsListWeek = userData!!.events.filter{s -> s.start_time > week}.toMutableList()
                EventsListWeek.forEach {s -> Log.d("EVENTLISTWeek", s.name)}
                val s = Stats()
                WeekHistogram.value = s.ComputeHistogram(EventsListWeek);
//                DayTimeSeries.value = s.ComputeTimeSeries(EventsListDay);

            }
        }
    }
    public fun LogEvent(eventName: String) {
        if (userData!!.uid != null) {

            if(eventName != currentActivity.value) {
//                val logevent = HashMap<String, kotlin.Any>()
//                logevent.put("Event", eventName)
//                logevent.put("StartTime", Calendar.getInstance().getTime())
//                Log.d("BUTTONEVENT", logevent.toString())
                val logevent = SingleEvent();
                logevent.start_time = Calendar.getInstance().getTime();
                logevent.name = eventName;
                Log.d("BUTTONEVENT", logevent.toString())

                userData!!.events.add(logevent);

                currentActivity.setValue(eventName);
                userData!!.currentActivity.name = eventName;
                userData!!.currentActivity.start_time = Calendar.getInstance().getTime()
                currentActivityStartTime.setValue(Calendar.getInstance().getTime() );
                db.collection("users")
                    .document(userData!!.uid!!).set(userData!!, SetOptions.merge())
            }
        }
    }

    public fun AddActivity(ActivityName: String) {
        if (userData!!.uid != null) {
            val Activity = HashMap<String, Int>();
            Activity.put(ActivityName, userData!!.activities.size)
            Log.d("Adding Activity", Activity.toString());
            userData!!.activities.add(Activity);
            activitiesNameList.add(ActivityName);
            activityNames.value = activitiesNameList;
            db.collection("users")
                .document(userData!!.uid!!).set(userData!!, SetOptions.merge())
        }
    }

    public fun getActivitiesNames() : MutableLiveData<MutableList<String>> {
        return activityNames;
    }
    public fun getCurrentActivity() : MutableLiveData<String> {
        return currentActivity;
    }
    public fun getCurrentActivityStartTime() : MutableLiveData<Date> {
        return currentActivityStartTime;
    }
    public fun getWeekHistogram() : MutableLiveData<HashMap<String,Long>> {
        return WeekHistogram;
    }
    public fun getCurrentActivityStartTimeMillis() : Long {
        return currentActivityStartTime.value!!.time;
    }
}

