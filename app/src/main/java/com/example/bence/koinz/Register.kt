package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    private val tag= "Register"
    private lateinit var auth: FirebaseAuth
    private var users=ArrayList<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.title="Register"
        auth= FirebaseAuth.getInstance()

        register.setOnClickListener{_ ->
            registernewuser()



        }
        backtologin.setOnClickListener{_->
            val intent =Intent( this, Login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent) //option to go back to login

        }
    }

    override fun onStart() {
        super.onStart()
        fetchUsers()
    }





    private fun saveuserName(username:String){

        val uid= FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("users/$uid")
        val user=User(uid,username)
        ref.setValue(user).addOnCompleteListener { Log.d(tag,"Successfully saved username!") }
        //saves username in real life database

    }
    private fun registernewuser(){
        val email = writeemail.text.toString()
        val password= writepassword.text.toString()
        val username= writename.text.toString()
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() ) return
                if( isMatchingDatabase(username)){
                    Toast.makeText(this,"The username you've entered is already taken!",Toast.LENGTH_SHORT).show()

                }
          else{
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(!it.isSuccessful) return@addOnCompleteListener

            //else if successful

            Log.d( tag, "Successfully created user with uid: ${it.result?.user?.uid}" )
            Toast.makeText(this,"Successfully created user with uid: ${it.result?.user?.uid}",Toast.LENGTH_SHORT).show()
            saveuserName(username)
            val backtomenu=Intent(this,MainActivity::class.java)
            startActivity(backtomenu) //takes user back to main menu after the user created a new account
                                      //display message on successful registration

        }.addOnFailureListener {
            Log.d(tag, "Failed to create user: ${it.message}")
            Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
            //display message on unsuccessful registration with appropriate error message
        }}
    }
    private fun isMatchingDatabase(name:String):Boolean{

        for(person in users){
            if(person.username==name){return true}
        }
        return false

    } //same function used in AddFriends, used to determine whether there exists a user already using the same username.
    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d(tag,"User added to the list!, $it")
                    val friend=it.getValue(User::class.java)!!

                        users.add(friend)


                }
            }


            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag,"User couldn't be added!")

            }

        })
    } //collects users that are already in the system (same function is used in AddFriends to add all users to an arraylist
}
class User(val uid:String,val username:String){
    constructor():this("","")

    override fun equals(other: Any?): Boolean {
        if (other is User){
          return this.uid==other.uid
        }
        return false
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }
} // Create for the data structure to be saved in the database
