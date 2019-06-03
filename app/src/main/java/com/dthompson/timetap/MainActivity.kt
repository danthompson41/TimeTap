package com.dthompson.timetap

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
    val activities = ArrayList<Pair<String, Int>>();
}

class MainActivity : AppCompatActivity() {
//    private val repo = Repository();
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = HashMap<String, kotlin.Any>();

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

        button.setOnClickListener {
            Repository.LogEvent("TestEvent2")
        };
        addEventbutton.setOnClickListener {
            Log.d("EventButton", "EventButton")
            val intent = Intent(this, Add_Trackingactivity_Main::class.java)
            startActivity(intent);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Repository.LoadUser(user!!);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
