package com.example.bence.koinz

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_friends.*
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Friends : AppCompatActivity() {
    private var wallet = Wallet()
    private var friendList = ArrayList<User>()
    private val tag = "Friends"
    private var displayindex = 1
    private var friendindex=1
    private var recievedcoinz=ArrayList<Coinz>() //list of coinz sent in the incoming requests
    private var sendersList=ArrayList<String>() //list of users the incoming requests are from
    private var giftids=ArrayList<String>() // coin id's alligning with sendersList and recieved coinz, helps to construct display of sent coin and sender name.
    private val user = FirebaseAuth.getInstance().currentUser
    private val useruid=user?.uid?:""
    private lateinit var currentuser:User
    private val prefs="MyPrefsFile"
    private var quid=0
    private var shil=0
    private var peny=0
    private var dolr=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        supportActionBar?.title=tag
        stepbackfriend.setOnClickListener { _ ->
            if (displayindex != 1) {
                displayindex--
                updatedisplay()
            }

        }

        stepfowardfriend.setOnClickListener { _ ->
            if (displayindex < wallet.size()) {
                displayindex++
                updatedisplay()
            }}
        stepbackfriend.setOnClickListener { _->
            if(displayindex!=1){
                displayindex--
                updatedisplay()
            }
        }
        prevfriend.setOnClickListener { _->
            if(friendindex!=1){
                friendindex--
                updatefrienddisplay()
            }
        }
        nextfriend.setOnClickListener { _->
            if(friendindex<friendList.size){
                friendindex++
                updatefrienddisplay()
            }
        }
        //buttons changing index if possible of displayed friend name and displayed coin, also updating display after change
            buttonwalletfriend.setOnClickListener{_->
                stepbackfriend.visibility= View.VISIBLE
                stepfowardfriend.visibility=View.VISIBLE
                coindisplayfriend.visibility=View.VISIBLE
                buttonwalletfriend.visibility=View.INVISIBLE
                buttonsendcoin.isEnabled=true
                updatedisplay()
                // helps in handling slight delay of asychronous getwallet function, by covering up wallet with a button. After button push the getwallet function is definitely finished.

            }
        buttonsendcoin.setOnClickListener{_->
            if(wallet.size()==0 || friendList.size==0)
            {
                if(wallet.size()==0)
                {Log.d(tag,"No coinz in the wallet!")}
                if(friendList.size==0)
                {
                    Log.d(tag,"No friend to send the coin to!")
                }
            }
            else{

            val fromid=currentuser.uid
                val fromname=currentuser.username

            val toid=friendList[friendindex-1].uid //takes currently displayed friend's id
            val ref= FirebaseDatabase.getInstance().getReference("messages/$toid").push()
            val key=ref.key

            val coin=wallet.getCoin(displayindex-1) //takes currently displayed coin
            if(coin!=null){

            if(fromid!="" && key!=null){

            val message=Message(key,fromid,fromname,toid,coin) //creates a Message of selected parameters to be sent


            ref.setValue(message).addOnCompleteListener {
                wallet.removeCoin(coin)
                wallet.savewallet() //save modified wallet to Firebase
                if (displayindex==wallet.size()+1 && displayindex!=1){displayindex--}
                updatedisplay()
                Log.d(tag,"Coin sent to friend: ${friendList[friendindex-1].username}")
                Toast.makeText(this,"Coin sent to friend: ${friendList[friendindex-1].username}",Toast.LENGTH_SHORT).show()
            } // send message to node messages belonging to the receivers uid.
                    .addOnFailureListener { Log.d(tag,"Message failed!") }
            }}
        }}
        buttonCollector.setOnClickListener { _ ->
            bankRecievedCoinz()
            updateCollectorButton()
        } //button to collect received coin

        }

    override fun onStart() {
        super.onStart()
        fetchCurUser()
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        peny=settings.getInt("penyNum",0)
        quid=settings.getInt("quidNum",0)
        shil=settings.getInt("shilNum",0)
        dolr=settings.getInt("dolrNum",0)
        wallet.getwallet()
        fetchfriends()
        listenforMessages()
    }
   private fun updatedisplay(){
        if(wallet.size()==0){
            coindisplayfriend.text="Wallet is empty!"
        }
        else{
            val coin = wallet.getCoin(displayindex - 1)
            if (coin != null) {
                val curr = coin.getcurrency()
                val value = (coin.getvalue()+0.5).toInt()

                coindisplayfriend.text = "Coin #$displayindex currency:$curr, value:$value"


            }
    }
} // update currently displayed coin of the wallet
   private fun updatefrienddisplay(){
        if(friendList.size==0){
            friendondisplay.text="You have no friends!"
        }
        else{
            val friend=friendList[friendindex-1]
            val name=friend.username
            friendondisplay.text=name
        }
    } //update currently displayed friend of the friendlist using the friendindex

    private fun listenforMessages(){

        val ref=FirebaseDatabase.getInstance().getReference("messages/$useruid")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message=p0.getValue(Message::class.java)
                if(message!=null){

                Log.d(tag,"Coin recieved: "+message.coin.toString())
                recievedcoinz.add(message.coin)
                    sendersList.add(message.fromname)
                    giftids.add(message.id)
                    //update ArrayLists helping with handling received coinz

                updateCollectorButton()}


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
    private fun bankRecievedCoinz(){

        val coin=recievedcoinz[0]
        recievedcoinz.removeAt(0)
        sendersList.removeAt(0)
            cointocurrecy(coin)
        val id=giftids[0] //used to delete the right message  after coin  it is collected

        val editor=getSharedPreferences(prefs, Context.MODE_PRIVATE).edit()
        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)
        editor.apply()

        val ref=FirebaseDatabase.getInstance().getReference("messages/$useruid/$id")
        ref.removeValue()
        giftids.removeAt(0)

    } //removes top received coin from all ArrayLists and removes corresponding node of the message from Firebase database
    private fun updateCollectorButton(){
        val coinnum=recievedcoinz.size

        if(coinnum>0) {
            val topcoin=recievedcoinz[0]
            val value=(topcoin.getvalue()+0.5).toInt()
            val curr=topcoin.getcurrency()
            val fromname=sendersList[0]
            buttonCollector.text = "$fromname sent you: $value in $curr"
            buttonCollector.isEnabled=true
        }
        else{
            buttonCollector.isEnabled=false
            buttonCollector.text="No coinz from friends"
        }
    } //updates collector button, always the 0th index of the ArrayList that saves the coin and sender
    private fun cointocurrecy(coin:Coinz){
        if(coin.getcurrency()=="DOLR")
        {
            peny=(peny+coin.getvalue()+0.5).toInt()
        }
        if(coin.getcurrency()=="SHIL")
        {
            shil=(shil+coin.getvalue()+0.5).toInt()
        }
        if(coin.getcurrency()=="QUID")
        {
            quid=(quid+coin.getvalue()+0.5).toInt()
        }
        if(coin.getcurrency()=="PENY")
        {
            peny=(peny+coin.getvalue()+0.5).toInt()
        }
        //adds coin value to the right currency variable based coin currency attribute
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
                        updatefrienddisplay()

                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        } // gets user's friends from friendlist node to fill up friendlist Arraylist
        else{
            Toast.makeText(this,"No user found please log in!",Toast.LENGTH_SHORT)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.friends_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.toAddFriends->{
                val intent= Intent(this,AddFriend::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //addfriend button sends user to AddFriend activity
            }
            R.id.backtomenu->{
                val intent=Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //back button sends user to MainActivity (main-menu)
            }
            R.id.toChallenges->{
                val intent=Intent(this,Challenges::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                // challenges button sends user to Challenges activity

            }

        }
        return super.onOptionsItemSelected(item)


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

    } // get's current user from the user's data node in the database

}

