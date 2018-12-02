package com.example.bence.koinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriend : AppCompatActivity() {
    private var users= ArrayList<User>()
    private val tag = "AddFriends"
    private var requesttoyou=ArrayList<FriendRequest>()
    private var requestindex=1
    private val user = FirebaseAuth.getInstance().currentUser
    private var currentuser=User()
    private var friendList=ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        supportActionBar?.title=tag
        buttonsendrequest.setOnClickListener {
            val friendname=enterfriendnamehere.text.toString()
            val friend =fetchUserByname(friendname)
            if (friend!=null){
                if(friendList.contains(friend)){
                    Toast.makeText(this,"You are already friends with this user!",Toast.LENGTH_LONG).show()
                }
                else {
                    sendRequesttoUser(friend)
                }
            }
            else
            {
                Log.d(tag,"No match found in the database")
                Toast.makeText(this,"Couldn't find name in our database!",Toast.LENGTH_SHORT).show()
            }
        }
        requestback.setOnClickListener {_->
            if(requestindex!=1)
            {
                requestindex--
                updateRequestDisplay()
            }

        }
        requestforward.setOnClickListener { _->
            if(requestindex<requesttoyou.size)
            {
                requestindex++
                updateRequestDisplay()
            }

        }
        buttondeclinerequest.setOnClickListener{_->
            if(requesttoyou.size!=0){
            val selectedRequest=requesttoyou[requestindex-1]
            deleteRequest(selectedRequest)
            requesttoyou.removeAt(requestindex-1)
            if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
            updateRequestDisplay()}
        }
        buttonacceptrequest.setOnClickListener{_->
            if(requesttoyou.size!=0){
            val selectedRequest=requesttoyou[requestindex-1]
            deleteRequest(selectedRequest)
            requesttoyou.removeAt(requestindex-1)
            saveFriendship(selectedRequest)
                if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
                updateRequestDisplay()
            fetchfriends()
        }}
    }

    override fun onStart() {
        super.onStart()
        fetchUsers()
        fetchfriends()
        listenForRequests()
    }

    fun updateRequestDisplay(){
        if(requesttoyou.size==0){
            displayrequest.text="No friend requests received!"
        }
        else{
            val request= requesttoyou[requestindex-1]
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
                    else{
                        currentuser=friend
                        users.add(friend)
                    }

                }
            }


            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag,"User couldn't be added!")

            }

        })
    }
    private fun isMatchingDatabase(name:String):Boolean{

        for(person in users){
            if(person.username==name){return true}
        }
        return false

    }
    private fun fetchUserByname(name:String):User?{
        if(isMatchingDatabase(name)){
            for(person in users){
                if(person.username==name){return person}
            }
            return null
        }
        else return null

    }
    private fun fetchUserByUid(uid:String):User?{

            for(person in users){
                if(person.uid==uid){return person}
            }
            return null


    }
    private fun sendRequesttoUser(person:User){
        val toid=person.uid
        val fromid=currentuser.uid
        val fromname=currentuser.username
        if(toid==fromid){
            Log.d(tag,"Can't send friend request to yourself!")
            Toast.makeText(this,"Can't send friend request to yourself!",Toast.LENGTH_SHORT).show()
        }
        else{
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$toid").push()
        val key=ref.key
        if(key!=null)
        {
            val request=FriendRequest(key,fromid,fromname,toid)
            ref.setValue(request).addOnCompleteListener {
                Log.d(tag,"Friend request sent to: ${person.username}")
                Toast.makeText(this,"Friend request sent to: ${person.username}",Toast.LENGTH_SHORT).show()


            }
        }}

    }
    private fun listenForRequests(){
        val useruid=user?.uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$useruid")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val request=p0.getValue(FriendRequest::class.java)
                if(request!=null)
                {
                    Log.d(tag,"Request received: ${request}")
                    requesttoyou.add(request)
                    updateRequestDisplay()
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })

    }

    private fun saveFriendship(friendRequest: FriendRequest){
        val fromid=friendRequest.fromid
        val toid=friendRequest.toid
        val refone=FirebaseDatabase.getInstance().getReference("friendlist/$fromid").push()
        val reftwo=FirebaseDatabase.getInstance().getReference("friendlist/$toid").push()
        val fromUser=fetchUserByUid(fromid)
        val toUser=fetchUserByUid(toid)
        refone.setValue(toUser).addOnCompleteListener {
            Log.d(tag,"Friend added to list! (number1)")
        }
        reftwo.setValue(fromUser).addOnCompleteListener {
            Log.d(tag,"Friend added to list! (number2)")
        }

    }
    private fun deleteRequest(friendRequest: FriendRequest){
        val toid=friendRequest.toid
        val id=friendRequest.id
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$toid/$id")
        ref.removeValue()

    }
    private fun fetchfriends(){
        val uid=user?.uid
        if(uid!=null)
        {
            val ref=FirebaseDatabase.getInstance().getReference("friendlist/$uid")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{
                        Log.d(tag,"User added to the list!, ${it.toString()}")
                        val friend=it.getValue(User::class.java)!!
                        friendList.add(friend)

                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
        else{
            Toast.makeText(this,"No user found please log in!",Toast.LENGTH_SHORT)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.backto_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.backtomenu->{
                val intent= Intent(this,Friends::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // adding sign out button, which signs out the user if clicked and redirects to Login activity
        }
        return super.onOptionsItemSelected(item)


    }

}
