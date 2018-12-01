package com.example.bence.koinz

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_friends.*
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Friends : AppCompatActivity() {
    private var wallet = Wallet()
    private var users= ArrayList<User>()
    private val tag = "Friends"
    private var displayindex = 1
    private var friendindex=1
    private var recievedcoinz=ArrayList<Coinz>()
    private val user = FirebaseAuth.getInstance().currentUser
    private val useruid=user?.uid?:""
    private val prefs="MyPrefsFile"
    private var quid=0
    private var shil=0
    private var peny=0
    private var dolr=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
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
            if(friendindex<users.size){
                friendindex++
                updatefrienddisplay()
            }
        }
            buttonwalletfriend.setOnClickListener{_->
                stepbackfriend.visibility= View.VISIBLE
                stepfowardfriend.visibility=View.VISIBLE
                coindisplayfriend.visibility=View.VISIBLE
                buttonwalletfriend.visibility=View.INVISIBLE
                buttonsendcoin.isEnabled=true
                updatedisplay()

            }
        buttonsendcoin.setOnClickListener{_->
            if(wallet.size()==0 || users.size==0)
            {
                if(wallet.size()==0)
                {Log.d(tag,"No coinz in the wallet!")}
                if(users.size==0)
                {
                    Log.d(tag,"No friend to send the coin to!")
                }
            }
            else{

            val fromid=useruid

            val toid=users.get(friendindex-1).uid
            val ref= FirebaseDatabase.getInstance().getReference("messages/$toid").push()
            val key=ref.key

            val coin=wallet.getCoin(displayindex-1)
            if(coin!=null){

            if(fromid!="" && key!=null){

            val message=Message(key,fromid,toid,coin)


            ref.setValue(message).addOnCompleteListener {
                wallet.removeCoin(coin)
                wallet.savewallet()
                if (displayindex==wallet.size()+1){displayindex--}
                updatedisplay()
                Log.d(tag,"Coin sent to friend: ${users.get(friendindex-1).username}")
                Toast.makeText(this,"Coin sent to friend: ${users.get(friendindex-1).username}",Toast.LENGTH_SHORT)
            }
                    .addOnFailureListener { Log.d(tag,"Message failed!") }
            }}
        }}
        buttonCollector.setOnClickListener { _ ->
            bankRecievedCoinz()
            recievedcoinz=ArrayList<Coinz>()
            updateCollectorButton()
        }

        }








    override fun onStart() {
        super.onStart()
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        peny=settings.getInt("penyNum",0)
        quid=settings.getInt("quidNum",0)
        shil=settings.getInt("shilNum",0)
        dolr=settings.getInt("dolrNum",0)
        wallet.getwallet()
        fetchUsers()
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
}
   private fun updatefrienddisplay(){
        if(users.size==0){
            friendondisplay.text="You have no friends!"
        }
        else{
            val friend= users.get(friendindex-1)
            val name=friend.username
            friendondisplay.text=name
        }
    }
    private fun fetchUsers() {

            val ref = FirebaseDatabase.getInstance().getReference("/users")

            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{
                            Log.d(tag,"User added to the list!, ${it.toString()}")
                        val friend=it.getValue(User::class.java)!!
                        if(user?.uid!=friend.uid){
                        users.add(friend)
                            updatefrienddisplay()
                        }

                        }
                    }


                override fun onCancelled(p0: DatabaseError) {
                    Log.d(tag,"User couldn't be added!")

                }

            })
    }
    private fun listenforMessages(){

        val ref=FirebaseDatabase.getInstance().getReference("messages/$useruid")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message=p0.getValue(Message::class.java)

                Log.d(tag,"Coin recieved: "+message?.coin.toString())
                recievedcoinz.add(message?.coin!!)

                updateCollectorButton()


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
        for(coin in recievedcoinz){
            cointocurrecy(coin)
        }
        val editor=getSharedPreferences(prefs, Context.MODE_PRIVATE).edit()
        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)
        editor.apply()
        val ref=FirebaseDatabase.getInstance().getReference("messages/$useruid")
        ref.removeValue()

    }
    private fun updateCollectorButton(){
        val coinnum=recievedcoinz.size
        if(coinnum>0) {
            buttonCollector.text = "$coinnum coin(z) arrived from friends!"
            buttonCollector.isEnabled=true
        }
        else{
            buttonCollector.isEnabled=false
            buttonCollector.text="No coinz from friends"
        }
    }
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
}

