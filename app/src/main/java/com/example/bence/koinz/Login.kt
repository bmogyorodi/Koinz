package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private val tag= "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginbutton.setOnClickListener{_ ->
            val email = writeemail.text.toString()
            val password= writepassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener


            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        //else if successful
                        Log.d(tag, "Successfully logged in user with uid: ${it.result?.user?.uid}")
                        Toast.makeText(this,"Successfully logged in user with uid: ${it.result?.user?.uid}",Toast.LENGTH_SHORT).show()
                        val backtomain=Intent(this,MainActivity::class.java)
                        startActivity(backtomain)
                    }
                    .addOnFailureListener {
                        Log.d(tag, "Failed to sign in user: ${it.message}")
                        Toast.makeText(this,"Failed to sign in user: ${it.message}",Toast.LENGTH_SHORT).show()
                    }




        }


        toregister.setOnClickListener{_ ->
            val intent =Intent( this, Register::class.java)
            startActivity(intent)

        }
}




    }

