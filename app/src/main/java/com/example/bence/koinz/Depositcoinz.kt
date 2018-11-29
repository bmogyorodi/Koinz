package com.example.bence.koinz

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_depositcoinz.*

class Depositcoinz : AppCompatActivity() {
    private var wallet = Wallet()
    private var shil = 0
    private var dolr = 0
    private var peny = 0
    private var quid = 0
    private val tag = "meltcoinz"
    private val prefs = "MyPrefsFile"
    private val coinzFile = "Coinzfile"
    private var displayindex = 1
    private var dailycollect=25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_depositcoinz)
        wallet.getwallet()
        updatedisplay()



        stepback.setOnClickListener { _ ->
            if (displayindex != 1) {
                displayindex--
                updatedisplay()
            }

        }

        stepfoward.setOnClickListener { _ ->
            if (displayindex < wallet.size()) {
                displayindex++
                updatedisplay()
            }


        }
        discard.setOnClickListener{_->
            if(wallet.size()!=0){
            if(displayindex==wallet.size()) {


                val coin = wallet.getCoin(displayindex - 1)
                if (coin != null) {
                    wallet.removeCoin(coin)
                    displayindex--
                    updatedisplay()
                }
            }
            else{
                val coin = wallet.getCoin(displayindex - 1)
                if (coin != null) {
                    wallet.removeCoin(coin)
                    updatedisplay()
                }

            }}
            else{
                Toast.makeText(this,"You don't have coinz!",Toast.LENGTH_SHORT).show()
            }
        }
        deposit.setOnClickListener{_->
            if(dailycollect!=0){
            if(wallet.size()!=0){
            val coin = wallet.getCoin(displayindex - 1)
            if(displayindex==wallet.size()) {




                if (coin != null) {
                    wallet.removeCoin(coin)
                    cointocurrecy(coin)
                    dailycollect--
                    displayindex--
                    updatedisplay()
                }
            }
            else{
                if (coin != null) {
                    wallet.removeCoin(coin)
                    cointocurrecy(coin)
                    dailycollect--
                    updatedisplay()
                }

            }}else{
                Toast.makeText(this,"You don't have coinz to deposit!",Toast.LENGTH_SHORT).show()
            }}else{
                Toast.makeText(this,"You can't deposit more coinz today!",Toast.LENGTH_SHORT).show()
            }

        }






    }

    override fun onStart() {
        super.onStart()
        val settings = getSharedPreferences(prefs, Context.MODE_PRIVATE)


        peny = settings.getInt("penyNum", 0)
        quid = settings.getInt("quidNum", 0)
        shil = settings.getInt("shilNum", 0)
        dolr = settings.getInt("dolrNum", 0)
        dailycollect=settings.getInt("dailycollect",25)





    }

    override fun onStop() {
        super.onStop()
        val editor=getSharedPreferences(prefs,Context.MODE_PRIVATE).edit()

        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)
        editor.putInt("dailycollect",dailycollect)
        editor.apply()
        wallet.savewallet()
    }

    private fun updatedisplay() {
        if(wallet.size()==0){
            coindisplay.text="Wallet is empty!"
        }
        else{
        val coin = wallet.getCoin(displayindex - 1)
        if (coin != null) {
            val curr = coin.getcurrency()
            val value = (coin.getvalue()+0.5).toInt()
            coindisplay.text = "Coin #$displayindex currency:$curr, value:$value"

        }}
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
    }


}