package com.example.bence.koinz

import java.util.ArrayList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Wallet constructor (private val capacity:Int=50 ) {

    private var coins = ArrayList<Coinz>(capacity) //wallet has capacity of 50 coinz on default
    private val tag="wallet"
    private val user = FirebaseAuth.getInstance().currentUser





    fun addCoin(coinz: Coinz){
        if(coins.size<50) {
            coins.add(coinz)
        }
        //adds  coin to the wallet

    }
    fun removeCoin(coinz: Coinz):Coinz? {
        return if(coins.contains(coinz))
        {
            coins.remove(coinz)
            coinz
        }
        else{
            null
        }
        //removes coin from wallet and returns it
    }


    fun getCoin(index:Int):Coinz?{
        return if(coins.size<=index){
            null
        }else{
            coins[index]
        }
        //accesses coin from wallet based on index
    }
    fun size():Int{
      return coins.size
        //returns amount of coinz in the wallet
    }
    fun savewallet(){
        if(user!=null){
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref= FirebaseDatabase.getInstance().getReference("wallets/$uid/wallet")

        ref.setValue(this.coins).addOnCompleteListener { Log.d(tag,"Successfully saved wallet!") }
        }



        //save wallet data for future use
    }
    fun getwallet(){
        //to get wallet from save
        if(user!=null) {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = FirebaseDatabase.getInstance().getReference("wallets/$uid/wallet")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{

                        val coin=it.getValue(Coinz::class.java)
                        if (coin!=null){
                            addCoin(coin)
                            Log.d(tag,"Coin added!")
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })






        } //every coin connected to the wallet in the database is added to the wallet in the application one by one


    }
    fun depriciateWallet(){
        for (i in coins){
            i.depriciate()
        }
        //depriciate the value of all coinz in the wallet
    }
    fun isfull():Boolean{
        return this.size()==this.capacity
    } //checks if the wallet is at full capacity
}