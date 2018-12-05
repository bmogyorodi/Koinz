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
    private var users= ArrayList<User>() //to store a list of all users
    private val tag = "AddFriends"
    private var requesttoyou=ArrayList<FriendRequest>() //to store friendrequests coming in for user
    private var requestindex=1 //index of the displayed request
    private val user = FirebaseAuth.getInstance().currentUser
    private var currentuser=User() //to store current user
    private var friendList=ArrayList<User>() //to store friends

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        supportActionBar?.title="Add Friends"
        buttonsendrequest.setOnClickListener {
            val friendname=enterfriendnamehere.text.toString()
            val friend =fetchUserByname(friendname)
            if (friend!=null){
                if(friendList.contains(friend)){ //checks if the user the request would go to is in the friend list
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
            enterfriendnamehere.text.clear()
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
// increase or decrease of request index if next requestindex-1 will point to a request in the ListArray
        }
        buttondeclinerequest.setOnClickListener{_->
            if(requesttoyou.size!=0){
            val selectedRequest=requesttoyou[requestindex-1]
            deleteRequest(selectedRequest) //deletes request from Firebase
            requesttoyou.removeAt(requestindex-1) //deletes request from the ListArray
            if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
            updateRequestDisplay()}

        }
        buttonacceptrequest.setOnClickListener{_->
            if(requesttoyou.size!=0){
            val selectedRequest=requesttoyou[requestindex-1]
            deleteRequest(selectedRequest)
            requesttoyou.removeAt(requestindex-1)
            saveFriendship(selectedRequest) //writes both ends of the friend request into each others friendlist
                if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
                updateRequestDisplay()
            fetchfriends()
        }}
    }

    override fun onStart() {
        super.onStart()
        fetchUsers() //to fill users ListArray with all existing users (to know if the username enter matches anyone in the database
        fetchfriends() // to fill friendlist ListArray (to know which users are already friends with the current user
        listenForRequests() // to download incoming requests from the Firebase database
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
    } // update the display text displaying a friend request (called usually when Arraylist is updated or requestindex changes
    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d(tag,"User added to the list!, $it")
                    val friend=it.getValue(User::class.java)!!
                    if(user?.uid!=friend.uid){
                        users.add(friend)

                    }
                    else{
                        currentuser=friend
                        users.add(friend)
                    }

                }
            } //adds all nodes found on /users (all users), also catches currentuser by checking uid, with signed in uid


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

    } //checking if a string equals a username of a user in the database
    private fun fetchUserByname(name:String):User?{
        if(isMatchingDatabase(name)){
            for(person in users){
                if(person.username==name){return person}
            }
            return null
        }
        else return null

    } // if string equals to a username of a user it returns that user
    private fun fetchUserByUid(uid:String):User?{

            for(person in users){
                if(person.uid==uid){return person}
            }
            return null


    } // same function as fetchUserByname but checking uid
    private fun sendRequesttoUser(person:User){
        val toid=person.uid
        val fromid=currentuser.uid
        val fromname=currentuser.username
        if(toid==fromid){
            Log.d(tag,"Can't send friend request to yourself!")
            Toast.makeText(this,"Can't send friend request to yourself!",Toast.LENGTH_SHORT).show()
        }
        else{
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$toid").push() //request is sent on the node of friendreqeusts which belongs to the toid user
        val key=ref.key //getting ref.key to be saved in the request (helps when request needs to be deleted from database
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
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$useruid") //reference points to users friendrequests node, where the reqeust would be saved if user's id was the toid attribute during sending
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val request=p0.getValue(FriendRequest::class.java)
                if(request!=null)
                {
                    Log.d(tag,"Request received: $request")
                    requesttoyou.add(request) //adds each incoming request to the requeststoyou ListArray and updates the displayed request
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
        } //saves sender to the friendlist of the  receiver
        reftwo.setValue(fromUser).addOnCompleteListener {
            Log.d(tag,"Friend added to list! (number2)")
        } //saves the receiver to the friendlist of the sender

    }
    private fun deleteRequest(friendRequest: FriendRequest){
        val toid=friendRequest.toid
        val id=friendRequest.id
        val ref=FirebaseDatabase.getInstance().getReference("friendrequests/$toid/$id")
        ref.removeValue() //removes friend request from database, called after accepting or declining friend request

    }
    private fun fetchfriends(){
        val uid=user?.uid
        if(uid!=null)
        {
            val ref=FirebaseDatabase.getInstance().getReference("friendlist/$uid")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{
                        Log.d(tag,"User added to the list!, $it")
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
            } // adding back to menu button, which returns user to the main friends activity
        }
        return super.onOptionsItemSelected(item)


    }

}
