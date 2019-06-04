package com.dthompson.timetap

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class dataModel() {
    val email: String? = null;
    val name: String? = null;
    val events = ArrayList<HashMap<String, kotlin.Any>>();
    val uid: String? = null;
    val activities = ArrayList<HashMap<String, Int>>();

}

object Repository {
    private var userData: dataModel? = null;
    private val currentUser = HashMap<String, kotlin.Any>();
    private val db = FirebaseFirestore.getInstance()
    private val activityNames = MutableLiveData<MutableList<String>>();
    private val activitiesNameList = mutableListOf<String>()


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
            val userRef = db.collection("users").document(s)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val newuser = document.data;
                        Log.d("NEWUSER", newuser.toString());
                        userData = document.toObject(dataModel::class.java)
                        Log.d("USERDATAEVENTS", userData?.events.toString());
                        Log.d("USERDATAEVENTS", userData?.activities.toString());
                        for (i in userData?.activities!!) {
                            for ((k,v) in i) {
                                activitiesNameList.add(k)
                            }
                        }
                        activityNames.value = activitiesNameList;

                        if (newuser != null) {
                            Log.d("GET", "Events " + userData?.events);
                        }
                    } else {
                        Log.d("GET", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("GET", "get failed with ", exception)
                }
        }
    }
    public fun LogEvent(eventName: String) {
        if (userData!!.uid != null) {
            val logevent = HashMap<String, kotlin.Any>();
            logevent.put("Event", eventName)
            logevent.put("StartTime", Calendar.getInstance().getTime())
            Log.d("BUTTONEVENT", logevent.toString());
            userData!!.events.add(logevent);
            db.collection("users")
                .document(userData!!.uid!!).set(userData!!, SetOptions.merge())
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
}

