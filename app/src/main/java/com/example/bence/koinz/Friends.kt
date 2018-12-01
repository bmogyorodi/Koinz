package com.example.bence.koinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_friends.*
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Friends : AppCompatActivity() {
    private var wallet = Wallet()
    private val tag = "Friends"
    private var displayindex = 1

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
            buttonwalletfriend.setOnClickListener{_->
                stepbackfriend.visibility= View.VISIBLE
                stepfowardfriend.visibility=View.VISIBLE
                coindisplayfriend.visibility=View.VISIBLE
                buttonwalletfriend.visibility=View.INVISIBLE
                updatedisplay()

            }
        buttonsendcoin.setOnClickListener{_->

            val uid=FirebaseAuth.getInstance().uid?:""

            val toid=writetargetid.text.toString()
            val ref= FirebaseDatabase.getInstance().getReference("messages/$uid").push()

            val coin=wallet.getCoin(displayindex-1)?:Coinz()


            val message=Message(ref.key!!,uid,toid,coin)

            ref.setValue(message).addOnCompleteListener { Log.d(tag,"Message sent!") }
                    .addOnFailureListener { Log.d(tag,"Message failed!") }}

        }






    override fun onStart() {
        super.onStart()
        wallet.getwallet()
    }
    fun updatedisplay(){
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
}

