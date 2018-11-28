package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    private val tag= "Register"
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth= FirebaseAuth.getInstance()

        register.setOnClickListener{_ ->
            registernewuser()
            val backtomenu=Intent(this,MainActivity::class.java)
            startActivity(backtomenu)



        }
        backtologin.setOnClickListener{_->
            val intent =Intent( this, Login::class.java)
            startActivity(intent)

        }
    }





    private fun saveuserName(){
        val username= writename.text.toString()
        val uid= FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("users/$uid")
        val user=User(uid,username)
        ref.setValue(user).addOnCompleteListener { Log.d(tag,"Successfully saved username!") }

    }
    private fun registernewuser(){
        val email = writeemail.text.toString()
        val password= writepassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(!it.isSuccessful) return@addOnCompleteListener

            //else if successful

            Log.d( tag, "Successfully created user with uid: ${it.result?.user?.uid}" )
            Toast.makeText(this,"Successfully created user with uid: ${it.result?.user?.uid}",Toast.LENGTH_SHORT).show()
            saveuserName()

        }.addOnFailureListener {
            Log.d(tag, "Failed to create user: ${it.message}")
            Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
        }
    }
}
class User(val uid:String,val username:String)
