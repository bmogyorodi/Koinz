package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    private val tag= "Register"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register.setOnClickListener{_ ->
             val name= writename.text.toString()
             val email = writeemail.text.toString()
            val password= writepassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d( tag, "Successfully created user with uid: ${it.result?.user?.uid}" )
                Toast.makeText(this,"Successfully created user with uid: ${it.result?.user?.uid}",Toast.LENGTH_SHORT)
            }.addOnFailureListener {
                Log.d(tag, "Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT)
            }


        }
        backtologin.setOnClickListener{_->
            val intent =Intent( this, Login::class.java)
            startActivity(intent)

        }
    }
}
