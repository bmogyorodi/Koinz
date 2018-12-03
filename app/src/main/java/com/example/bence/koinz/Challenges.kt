package com.example.bence.koinz

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_challanges.*
import java.util.*

class Challenges : AppCompatActivity() {
    private val tag="Challenges"
    private val user = FirebaseAuth.getInstance().currentUser
    private val useruid=user?.uid?:""
    private lateinit var currentuser:User
    private var friendList = ArrayList<User>()
    private var requesttoyou=ArrayList<ChallengeRequest>()
    private var requestindex=1
    private var today=""
    private val prefs="MyPrefsFile"
    private var challengegold=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challanges)
        supportActionBar?.title="Challanges"
        buttonsendchallenge.setOnClickListener{
            val friendname=writechallange.text.toString()
            val friend =fetchFriendByname(friendname)
            if (friend!=null){

                    sendChallengetoUser(friend)

            }
            else
            {
                Log.d(tag,"User wasn't found in your friendlist")
                Toast.makeText(this,"User wasn't found in your friendlist!",Toast.LENGTH_SHORT).show()
            }
            writechallange.text.clear()
        }
        prevchallenger.setOnClickListener { _->
            if(requestindex!=1)
            {
                requestindex--
                updateChallangedisplay()
            }
        }
        nextchallenger.setOnClickListener { _->
            if(requestindex<requesttoyou.size)
            {
                requestindex++
                updateChallangedisplay()
            }

        }
        buttondeclineChallenge.setOnClickListener { _->
        if(requesttoyou.size!=0){
            val selectedRequest=requesttoyou[requestindex-1]
            deleteRequest(selectedRequest)
            requesttoyou.removeAt(requestindex-1)
            if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
            updateChallangedisplay()}
        }
        buttonacceptChallenge.setOnClickListener{ _->
            if(requesttoyou.size!=0){
                val selectedRequest=requesttoyou[requestindex-1]
                deleteRequest(selectedRequest)
                requesttoyou.removeAt(requestindex-1)
                saveChallange(selectedRequest)
                if (requestindex==requesttoyou.size+1 && requestindex!=1){requestindex--}
                updateChallangedisplay()

            }}
        buttoncollectchallengegold.setOnClickListener { _->
            val settings=getSharedPreferences(prefs, Context.MODE_PRIVATE)
            var gold= settings.getInt("goldNum",0)
            gold+=challengegold
            val editor=settings.edit()
            editor.putInt("goldNum",gold)
            editor.apply()
            challengegold=0
        }
    }

    override fun onStart() {
        super.onStart()
        fetchdate()
        fetchCurUser()
        fetchfriends()
        listenForChallanges()
        fetchActiveChallenges()
    }
    private fun fetchdate(){
        val setting=getSharedPreferences(prefs, Context.MODE_PRIVATE)
        today=setting.getString("lastDownloadDate","")
    }
    private fun fetchCurUser(){

        val ref = FirebaseDatabase.getInstance().getReference("/users/$useruid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d(tag,"Got current user!, $p0")
                currentuser=p0.getValue(User::class.java)!!






            }
        })

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
            Toast.makeText(this,"No user found please log in!", Toast.LENGTH_SHORT)
        }
    }
    private fun updateChallangedisplay(){
        if(requesttoyou.size==0){
            challangesdisplay.text="You have no challenge requests!"
        }
        else{
            val request=requesttoyou[requestindex-1]
            val name=request.fromname
            val date=request.ondate
            challangesdisplay.text="$name challenged you to a duel on $date!"
        }
    }
    private fun isMatchingDatabase(name:String):Boolean{

        for(person in friendList){
            if(person.username==name){return true}
        }
        return false

    }
    private fun fetchFriendByname(name:String):User?{
        if(isMatchingDatabase(name)){
            for(person in friendList){
                if(person.username==name){return person}
            }
            return null
        }
        else return null

    }
    private fun sendChallengetoUser(person:User){
        val toid=person.uid
        val toname=person.username
        val fromid=currentuser.uid
        val fromname=currentuser.username
        if(toid==fromid){
            Log.d(tag,"Can't send challenge request to yourself!")
            Toast.makeText(this,"Can't send challenge request to yourself!",Toast.LENGTH_SHORT).show()
        }
        else{
            val ref=FirebaseDatabase.getInstance().getReference("challengerequests/$toid").push()
            val key=ref.key
            if(key!=null)
            {
                val request=ChallengeRequest(key,fromid,fromname,today,toid,toname)
                ref.setValue(request).addOnCompleteListener {
                    Log.d(tag,"Challenge request sent to: ${person.username}")
                    Toast.makeText(this,"Challenge request sent to: ${person.username}",Toast.LENGTH_SHORT).show()


                }
            }}

    }
    private fun listenForChallanges(){
        val useruid=user?.uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("challengerequests/$useruid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val request=p0.getValue(ChallengeRequest::class.java)
                if(request!=null)
                {
                    Log.d(tag,"Request received: $request")
                    requesttoyou.add(request)
                    updateChallangedisplay()
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

    private fun deleteRequest(challange: ChallengeRequest){
        val toid=challange.toid
        val id=challange.id
        val ref=FirebaseDatabase.getInstance().getReference("challengerequests/$toid/$id")
        ref.removeValue()

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
    private fun saveChallange(challange: ChallengeRequest){
        val fromid=challange.fromid
        val toid=challange.toid

        val refone=FirebaseDatabase.getInstance().getReference("activeChallenges/$fromid").push()
        val reftwo=FirebaseDatabase.getInstance().getReference("activeChallenges/$toid").push()
        val key1=refone.key?:""
        val key2=reftwo.key?:""
        challange.resetid(key1)
        refone.setValue(challange).addOnCompleteListener {
            Log.d(tag,"Challenge added to list! (number1)")
        }
        challange.resetid(key2)
        reftwo.setValue(challange).addOnCompleteListener {
            Log.d(tag,"Challenge added to list! (number2)")
        }
    }

    private fun fetchActiveChallenges(){
        val ref=FirebaseDatabase.getInstance().getReference("activeChallenges/$useruid")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val challenge=p0.getValue(ChallengeRequest::class.java )
                if (challenge!=null){
                    if(challenge.ondate!=today){
                        concludeChallenge(challenge)
                    }
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
    private fun concludeChallenge(challenge: ChallengeRequest){
        val playerone=challenge.fromid
        val playertwo=challenge.toid
        val id=challenge.id
        var pointsone=0
        var pointstwo=0
        var winner=""
        val refchallenge=FirebaseDatabase.getInstance().getReference("activeChallenges/$useruid/$id")

        val date=challenge.ondate
        val refplayerone=FirebaseDatabase.getInstance().getReference("collection/$date/$playerone")
        val refplayertwo=FirebaseDatabase.getInstance().getReference("collection/$date/$playertwo")
        refplayerone.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                pointsone= p0.getValue(Int::class.java)?:0
            }

        })
        refplayertwo.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                 pointstwo=p0.getValue(Int::class.java)?:0
            }

        })
        if(pointsone>pointstwo){
            winner=playerone
        }
        else{
            if(pointstwo>pointsone)
            {
                winner=playertwo
            }
        }
        if(winner==useruid){
            challengegold+=500
            Log.d(tag,"You won a challenge!")
            buttoncollectchallengegold.text="You won $challengegold gold from challenges! \n Collect here!"
            refchallenge.removeValue()

        }
        else
        {
            Log.d(tag,"You lost a challange")
            refchallenge.removeValue()
        }





    }

}
