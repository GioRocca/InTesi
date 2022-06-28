package com.example.inTesi.prova.models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import com.example.inTesi.R
import com.google.firebase.auth.FirebaseAuth
import com.example.inTesi.prova.activities.SplashActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FirebaseWrapper(private val context: Context) {
    private var auth: FirebaseAuth = Firebase.auth
    private val TAG : String = FirebaseWrapper::class.simpleName.toString()
    fun isAuth(): Boolean {
        return auth.currentUser != null
    }

    /*fun signUp(email: String,password: String)
    {
        this.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    logSuccess()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Sign-up failed.${task.exception!!.message}\"",Toast.LENGTH_LONG).show()

                }
            }
        }

    fun signIn(email: String,password: String)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    logSuccess()
                //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    //Toast.makeText(baseContext, "Authentication failed.",
                    //Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun logSuccess() {
        val intent : Intent = Intent(this.context, SplashActivity::class.java)
        context.startActivity(intent)
    }
    */

    fun getUid() : String?{
        return auth.currentUser?.uid
    }

    fun mergeTesiWithFirebaseInfo(context: Context,tesi : List<MyTesi>)
    {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val mapFirebaseTesi = HashMap<String,FirebaseDbWrapper.Companion.FirebaseTesi>()

        GlobalScope.launch {
            FirebaseDbWrapper(context).readDbData(object : FirebaseDbWrapper.Companion.FirebaseReadCallback{
                override fun onDataChangeCallback(snapshot: DataSnapshot) {
                    Log.d("onDataChangeCallback","invoked")
                    for(child in snapshot.children)
                    {
                        val firebaseTesi : FirebaseDbWrapper.Companion.FirebaseTesi = child.getValue(FirebaseDbWrapper.Companion.FirebaseTesi::class.java)!!
                        mapFirebaseTesi.put(firebaseTesi.title,firebaseTesi)
                    }
                    lock.withLock {
                    condition.signal()
                    }
                }

                override fun onCancelledCallback(error: DatabaseError) {
                    Log.d("onCancelledCallback","invoked")
                }

            })
        }
        lock.withLock {
        condition.await()
        }
        for(myTesi in tesi){
            myTesi.version_id = mapFirebaseTesi[myTesi.title]?.version_id?:myTesi.version_id
            myTesi.collaborator = mapFirebaseTesi[myTesi.title]?.collaborator?:myTesi.collaborator
            myTesi.relator = mapFirebaseTesi[myTesi.title]?.relator?:myTesi.relator

        }
    }
}

class FirebaseDbWrapper(private val context: Context)
{
    private val TAG : String = FirebaseDbWrapper::class.simpleName.toString()
    private val CHILD : String = "Tesi"


    private fun getDb():DatabaseReference?{
        val ref = Firebase.database.getReference(CHILD)
        val uid = FirebaseWrapper(context).getUid()
        if(uid == null) {
            return null;
        }
        return ref.child(uid)
    }
    fun writeDbData(firebasetesi : FirebaseTesi) {

        val ref = getDb()
        if (ref == null)
            return;
        else
        {
            val ft = FirebaseTesi("Teacher", "Intesi", 1, "Bava Flavio", "Merlo")

            ref.child(firebasetesi.version_id.toString()).setValue(firebasetesi)

        }
    }

    fun readDbData(callback : FirebaseReadCallback) {
        val ref = getDb()
        if (ref == null)
            return
        else
        {
            ref.addValueEventListener(FirebaseReadListener(callback))



        }
    }
    companion object{
        class FirebaseTesi(val role : String,val title : String,val version_id : Long,val collaborator : String,val relator : String){

        }
        class FirebaseReadListener(val callback : FirebaseReadCallback) : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                callback.onDataChangeCallback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onCancelledCallback(error)
            }

        }
        interface FirebaseReadCallback {
            fun onDataChangeCallback(snapshot: DataSnapshot);
            fun onCancelledCallback(error: DatabaseError);
        }
    }
}