package com.dthompson.timetap

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.internal.BottomNavigationMenu
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.View
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val RC_SIGN_IN = 1
var userId: String? = null;
var events = ArrayList<HashMap<String, kotlin.Any>>();
var eventsobject: userdata? = null;

class userdata {
    val events = ArrayList<HashMap<String, kotlin.Any>>();
    val userId: String? = null;
    val activities = ArrayList<HashMap<String, Int>>();
}

class MainActivity : AppCompatActivity() {
    val mOnNavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener? = BottomNavigationView.OnNavigationItemSelectedListener {
        item -> when(item.itemId) {
        R.id.home -> {
            Log.d("Switch","Home")
            replaceFragment(TimeActivityListFragment())
            return@OnNavigationItemSelectedListener true
        };
        R.id.addActivity -> {
            Log.d("Switch","AddActivity")
            replaceFragment(AddTrackingactivity())
            return@OnNavigationItemSelectedListener true
        };
        R.id.graph -> {
            Log.d("Switch","Graph")
            replaceFragment(GraphFragment())
            return@OnNavigationItemSelectedListener true
        };
        R.id.device -> {
            Log.d("Switch","Device")
            replaceFragment(DeviceFragment())
            return@OnNavigationItemSelectedListener true
        };

    }
        return@OnNavigationItemSelectedListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
            //AuthUI.IdpConfig.GoogleBuilder().build())
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )

        setContentView(R.layout.activity_main)
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }
     fun notify_activity_add() {
        replaceFragment(TimeActivityListFragment())
    }
    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainContentGoesHere, fragment)
        fragmentTransaction.commit()
    }
    fun logCalendar() {
        if (Repository.getCurrentActivity().value != "") {
            val intent: Intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Repository.getCurrentActivityStartTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, Calendar.getInstance().getTime().time)
                .putExtra(CalendarContract.Events.TITLE, Repository.getCurrentActivity().value)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            startActivityForResult(intent, RESULT_OK)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Repository.getActivitiesNames().observe(this,
                    android.arch.lifecycle.Observer { data ->
                        Log.d("ObserverTest", "hey" + data.toString())
                    })
                Repository.LoadUser(user!!);

                var ListFragment = TimeActivityListFragment()
                var CurrentActivityFragment = CurrentActivityDisplayFragment();
                ListFragment.setArguments(intent.extras)
                // get the reference to the FragmentManger object
                val fragManager = supportFragmentManager
                // get the reference to the FragmentTransaction object
                val transaction = fragManager.beginTransaction()
                // add the fragment into the transaction
                transaction.add(R.id.mainContentGoesHere, ListFragment)
                transaction.add(R.id.currentactivitygoeshere, CurrentActivityFragment);
                // commit the transaction.
                transaction.commit()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}

