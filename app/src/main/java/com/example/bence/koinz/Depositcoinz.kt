package com.example.bence.koinz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
    private var displayindex = 1
    private var dailycollect=25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_depositcoinz)
        supportActionBar?.title="Wallet"
        wallet.getwallet() // get wallet from database




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


        } // updates the coin displayed on the screen (moving the index in the wallet), displaying currency and value
        discard.setOnClickListener{_->
            //button removes coin from wallet, sets the displayindex back in case it was on the last index so the displayindex won't get out of bound
            if(wallet.size()!=0){



                val coin = wallet.getCoin(displayindex - 1)
                if (coin != null) {
                    wallet.removeCoin(coin)
                    if(wallet.size()+1==displayindex && displayindex!=1){displayindex--}
                    updatedisplay()
                    Log.d(tag,"Coin discarded ${coin.getid()}")
                }



            savechanges()
            }
            else{
                Toast.makeText(this,"You don't have coinz!",Toast.LENGTH_SHORT).show()
            }
        }
        deposit.setOnClickListener{_->
            // does the same as discard, but also uses the cointocurrency function to add to the treasure of the user
            // checks dailycollect to check if the user can deposit coinz today, also decreases the variable by one after a deposit
            if(dailycollect!=0){
            if(wallet.size()!=0){
            val coin = wallet.getCoin(displayindex - 1)





                if (coin != null) {
                    wallet.removeCoin(coin)
                    cointocurrecy(coin)
                    dailycollect--
                    if (displayindex==wallet.size()+1 && displayindex!=1){displayindex--}
                    updatedisplay()
                    updateDepositdisplay()
                    Log.d(tag,"Coin deposited ${coin.getid()}")
                }


             savechanges()
            }else{
                Toast.makeText(this,"You don't have coinz to deposit!",Toast.LENGTH_SHORT).show()
            }}else{
                Toast.makeText(this,"You can't deposit more coinz today!",Toast.LENGTH_SHORT).show()
                //warning message if the deposit limit is reached
            }


        }
        buttonopenwallet.setOnClickListener { _->
            stepback.visibility= View.VISIBLE
            stepfoward.visibility=View.VISIBLE
            coindisplay.visibility=View.VISIBLE
            buttonopenwallet.visibility=View.INVISIBLE
            updatedisplay()
        } //button used to give time for the updatedisplay function to be called after getwallet function is finished. when pressed disappears and rest of the activity becomes visible






    }

    override fun onStart() {
        super.onStart()
        val settings = getSharedPreferences(prefs, Context.MODE_PRIVATE)


        peny = settings.getInt("penyNum", 0)
        quid = settings.getInt("quidNum", 0)
        shil = settings.getInt("shilNum", 0)
        dolr = settings.getInt("dolrNum", 0)
        dailycollect=settings.getInt("dailycollect",25)
        updateDepositdisplay()
        // gets current currencies held by user to modify and the amount of coinz they can collect today





    }

    override fun onStop() {
        super.onStop()
        savechanges()
        //saves content of wallet and new currency numbers held by user, and daily collectable coinz
    }

    @SuppressLint("SetTextI18n")
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


        }} //updates the values on the coin which is shown in the wallet with index, currency and value, called everytime the index is modified
    }
    private fun cointocurrecy(coin:Coinz){
        if(coin.getcurrency()=="DOLR")
        {
            dolr=(dolr+coin.getvalue()+0.5).toInt()
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

    private fun savechanges(){
        val editor=getSharedPreferences(prefs,Context.MODE_PRIVATE).edit()

        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)
        editor.putInt("dailycollect",dailycollect)
        editor.apply()
        wallet.savewallet()

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.wallet_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.toMap->{
                val intent= Intent(this,Map::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            // map button, sends user to Map activity
            R.id.toconverter->{
                val intent=Intent(this,Currency::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // Bank button, sends user to Currency activity
            R.id.backtomenu->{
                val intent= Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // back button sends user to MainActivity (main-menu)
        }
        return super.onOptionsItemSelected(item)


    }
    private fun updateDepositdisplay(){
        displaydailydepositleft.text="You can make $dailycollect more deposits today"
    }
    //Updates count of available deposits today, called after deposit


}