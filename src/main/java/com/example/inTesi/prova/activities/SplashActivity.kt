package com.example.inTesi.prova.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.inTesi.R
import com.example.inTesi.prova.models.FirebaseWrapper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class SplashActivity : AppCompatActivity() {
        private val TAG = SplashActivity::class.simpleName

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

                Log.d(TAG,"Splash started")
                val firebaseWrapper: FirebaseWrapper = FirebaseWrapper(this)
                if(!firebaseWrapper.isAuth()){
                        val intent = Intent(this,RegistrationActivity::class.java)
                        this.startActivity(intent)
                        finish()
                        return

                }
        // Start Main Activity
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finish()
    }

}