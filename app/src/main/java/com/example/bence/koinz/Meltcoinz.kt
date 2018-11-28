package com.example.bence.koinz

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_meltcoinz.*

class Meltcoinz : AppCompatActivity() {
    private var wallet=Wallet()
    private var shil=0
    private var dolr=0
    private var peny=0
    private var quid=0
    private val tag="meltcoinz"
    private val prefs="MyPrefsFile"
    private val coinzFile="Coinzfile"
    private var displayindex=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meltcoinz)
       // stepback.setOnClickListener{_->
         //   if(displayindex!=1)
         //   {
           //     displayindex--
               // updatedisplay()
         //   }

       // }

       // stepfoward.setOnClickListener{_->
        //    if (displayindex<wallet.size())
         //   {
         //       displayindex++
                //updatedisplay()
          //  }


        }


//        updatedisplay()



    }

   // override fun onStart() {
     //   super.onStart()
     //   val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
      //  peny=settings.getInt("penyNum",0)
      //  quid=settings.getInt("quidNum",0)
      //  shil=settings.getInt("shilNum",0)
      //  dolr=settings.getInt("dolrNum",0)
      //  wallet.getwallet()














    

   // private fun updatedisplay(){
     //   val coin=wallet.getCoin(displayindex-1)
     //   if(coin!=null){
      //  val curr =coin.getcurrency()
      //  val value= coin.getvalue()
       // coindisplay.text= "Coin #$displayindex currency:$curr, value:$value"

   // }}

