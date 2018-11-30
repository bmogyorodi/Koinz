package com.example.bence.koinz

import java.util.ArrayList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import android.content.Context
import android.widget.Toast
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
        if(coins.contains(coinz))
        {
            coins.remove(coinz)
            return coinz
        }
        else{return null}
        //removes coin from wallet and returns it
    }
    fun getCoin(index:Int):Coinz?{
        if(!(coins.size>index)){
            return null
        }else{return coins.get(index)}
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






        }


    }
    fun depriciateWallet(){
        for (i in coins){
            i.depriciate()
        }
        //depriciate the value of all coinz in the wallet
    }
    fun isfull():Boolean{
        return this.size()==this.capacity
    }
}