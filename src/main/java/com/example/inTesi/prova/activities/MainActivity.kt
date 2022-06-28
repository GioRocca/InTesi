package com.example.inTesi.prova.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.inTesi.R
import com.example.inTesi.prova.models.FirebaseDbWrapper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var mToolbar : androidx.appcompat.widget.Toolbar
    lateinit var floatingActionButton : FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        mToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mToolbar)
        //FirebaseDbWrapper(this).writeDbData()
        /*
        */
        floatingActionButton = findViewById(R.id.float_btn)
        floatingActionButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val addfile = Intent(applicationContext, UploadPDF::class.java)
                startActivity(addfile)
                finish()
            }
        });
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }
}