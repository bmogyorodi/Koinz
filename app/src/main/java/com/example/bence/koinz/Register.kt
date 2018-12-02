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
        supportActionBar?.title="Register"
        auth= FirebaseAuth.getInstance()

        register.setOnClickListener{_ ->
            registernewuser()
            val backtomenu=Intent(this,MainActivity::class.java)
            startActivity(backtomenu) //takes user back to main menu after the user created a new account



        }
        backtologin.setOnClickListener{_->
            val intent =Intent( this, Login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent) //option to go back to login

        }
    }





    private fun saveuserName(){
        val username= writename.text.toString()
        val uid= FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("users/$uid")
        val user=User(uid,username)
        ref.setValue(user).addOnCompleteListener { Log.d(tag,"Successfully saved username!") }
        //saves username in real life database

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
            saveuserName() //display message on successful registration

        }.addOnFailureListener {
            Log.d(tag, "Failed to create user: ${it.message}")
            Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
            //display message on unsuccessful registration with appropriate error message
        }
    }
}
class User(val uid:String,val username:String){
    constructor():this("","")

    override fun equals(other: Any?): Boolean {
        if (other is User){
          return this.uid==other.uid
        }
        return false
    }
} // Create for the data structure to be saved in the database
