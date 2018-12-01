package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriend : AppCompatActivity() {
    private var users= ArrayList<User>()
    private val tag = "AddFriends"
    private var requesttoyou=ArrayList<FriendRequest>()
    private var requestindex=1
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
    }

    override fun onStart() {
        super.onStart()
    }
    fun updateRequestDisplay(){
        if(requesttoyou.size==0){
            displayrequest.text="No friend requests received!"
        }
        else{
            val request= requesttoyou.get(requestindex-1)
            val name=request.fromname
            displayrequest.text="Friend request from: $name"
        }
    }
    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d(tag,"User added to the list!, ${it.toString()}")
                    val friend=it.getValue(User::class.java)!!
                    if(user?.uid!=friend.uid){
                        users.add(friend)

                    }

                }
            }


            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag,"User couldn't be added!")

            }

        })
    }
    private fun isMatchinDatabase(name:String):Boolean{

        for(person in users){
            if(person.username==name){return true}
        }
        return false

    }
    private fun fetchEnteredUser(name:String):User?{
        if(isMatchinDatabase(name)){
            for(person in users){
                if(person.username==name){return person}
            }
            return null
        }
        else return null

    }
    private fun sendRequesttoUser(person:User){

    }

}
