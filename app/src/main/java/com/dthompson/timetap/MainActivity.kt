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
var userId : String? = null;
val events = ArrayList<HashMap<String, kotlin.Any>>();


class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = HashMap<String, kotlin.Any>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //db = FirebaseFirestore.getInstance()
//      Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)

        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            Log.d("OnClick", "userid");
            if (userId !== null) {
                val ID : String = currentUser["uid"].toString();
                val logevent = HashMap<String, kotlin.Any>();
                logevent.put("UUID", userId.toString())
                logevent.put("Event","TestEvent")
                logevent.put("StartTime", Calendar.getInstance().getTime())
                Log.d("BUTTONEVENT",logevent.toString());
                events.add(logevent);
                currentUser.put("Events", events)
                db.collection("users")
                    .document(ID).set(currentUser, SetOptions.merge())
                db.collection("events")
                    .add(logevent)
                    .addOnSuccessListener {
                        Log.d("SAVESUCCESS","saved");
                    }
                    .addOnFailureListener {
                        Log.d("SAVEFAIL", "failed");
                    }


            }
        };

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {

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
                    Log.d("USERSTUFF",user.displayName);
                    Log.d("USERSTUFF",user.email);
                    Log.d("USERSTUFF",user.photoUrl.toString());
                    Log.d("USERSTUFF",user.isEmailVerified.toString());
                    Log.d("USERSTUFF",user.uid);
                    userId = currentUser["uid"].toString();
                    val s : String = currentUser["uid"].toString();
                    db.collection("users")
                        .document(s).set(currentUser, SetOptions.merge())
                }


                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
