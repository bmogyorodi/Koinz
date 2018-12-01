package com.example.bence.koinz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_currency.*

class Currency : AppCompatActivity() {

    private var gold=0
    private var peny=0
    private var quid=0
    private var shil=0
    private var dolr=0
    private var penyx=0.0
    private var quidx=0.0
    private var shilx=0.0
    private var dolrx=0.0
    private var penysel=0
    private var quidsel=0
    private var shilsel=0
    private var dolrsel=0
    private var allsel=0
    private var addedgold=0
    private var allowedexchange=10
    private var exchangeenabled=true
    private var extracost=100
    // currency names are the amount the player has, curencyx is the exchange rate between gold and currency, currencysel is the amount from that currency selected for conversion
    private val prefs= "MyPrefsFile"

    private fun updateaddgold(){
        addedgold= (penyx*penysel+dolrx*dolrsel+shilx*shilsel+quidx*quidsel).toInt()
        goldfromex.text="$addedgold"
        allsel=dolrsel+shilsel+quidsel+penysel
        //update the value of addedgold after button push, plus update the value of allsel

    }
    @SuppressLint("SetTextI18n")
    private fun updatetreasury(){
        treasury.text="Gold:$gold Peny:$peny Quid:$quid Shil:$shil Dolr:$dolr"

        //update treasury values at the start of activity and after exchange of currency
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        updolr.setOnClickListener{_ ->
            if(dolrsel<dolr && allowedexchange>allsel)
            {
            dolrsel++
            condolr.text="$dolrsel"
            updateaddgold()
            }
        }
        downdolr.setOnClickListener{_ ->
            if(dolrsel>0)
            {
                dolrsel--
                condolr.text="$dolrsel"
                updateaddgold()
            }


        }
        uppeny.setOnClickListener{_ ->
            if(penysel<peny && allowedexchange>allsel){
            penysel++
            conpeny.text="$penysel"
            updateaddgold()}
        }
        downpeny.setOnClickListener{_ ->
            if(penysel>0)
            {
                penysel--
                conpeny.text="$penysel"
                updateaddgold()
            }


        }
        upquid.setOnClickListener{_ ->
            if (quidsel<quid && allowedexchange>allsel){
                quidsel++
            conquid.text="$quidsel"
            updateaddgold()}
        }
        downquid.setOnClickListener{_ ->
            if(quidsel>0)
            {
                quidsel--
                conquid.text="$quidsel"
                updateaddgold()
            }


        }
        upshil.setOnClickListener{_ ->
            if (shilsel<shil && allowedexchange>allsel) {
                shilsel++
                conshil.text = "$shilsel"
                updateaddgold()
            }
        }
        downshil.setOnClickListener{_ ->
            if(shilsel>0)
            {
                shilsel--
                conshil.text="$shilsel"
                updateaddgold()
            }


        }
        exchange.setOnClickListener{_ ->
            if(exchangeenabled)
            {
            gold += addedgold
            peny -= penysel
            quid -= quidsel
            shil -= shilsel
            dolr -= dolrsel
             penysel=0
             quidsel=0
             shilsel=0
             dolrsel=0
             allsel=0
            //exchange operation carried out on selected amounts, after which selector variables are set back to 0
            conpeny.text="0"
            condolr.text="0"
            conquid.text="0"
            conshil.text="0"
            //reset displays as well
            updateaddgold()
            updatetreasury()
            savetreasury()
                exchangeenabled=false
                buynewexchange.isEnabled=true
                buynewexchange.visibility= View.VISIBLE
                buynewexchange.text="extra exchange:$extracost G"

            }
            else
            {
                Toast.makeText(this,"Pay exchange fee to enable exchanging currencies!",Toast.LENGTH_SHORT).show()
            }

        }
        buynewexchange.setOnClickListener { _->

            if(gold>extracost){
            gold-=extracost
            exchangeenabled=true
            extracost+=100
            buynewexchange.isEnabled=false
            buynewexchange.visibility= View.INVISIBLE
            updatetreasury()}
            else
            {
                Toast.makeText(this,"You don't have enough gold to purchase extra exchanges for today!",Toast.LENGTH_SHORT).show()
            }
        }



    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        gold=settings.getInt("goldNum",0)
        peny=settings.getInt("penyNum",0)
        quid=settings.getInt("quidNum",0)
        shil=settings.getInt("shilNum",0)
        dolr=settings.getInt("dolrNum",0)
        penyx= settings.getFloat("penyEX", 10F).toDouble()
        quidx= settings.getFloat("quidEX", 10F).toDouble()
        shilx= settings.getFloat("shilEX", 10F).toDouble()
        dolrx= settings.getFloat("dolrEX", 10F).toDouble()
        exchangeenabled=settings.getBoolean("exisallowed",true)
        extracost=settings.getInt("extracost",100)
        if(!exchangeenabled){
            buynewexchange.isEnabled=true
            buynewexchange.visibility= View.VISIBLE
            buynewexchange.text="extra exchange:$extracost G"
        }
        //get users treasure, amount of each currency and gold, plus the dailyrates of currencies from the prefsfile
        updatetreasury()
        exrate.text="Exchange rates: \n Peny: $penyx \n Quid: $quidx \n Shil: $shilx \n Dolr: $dolrx"

    }

    override fun onStop() {
        super.onStop()
        savetreasury()
        val editor=getSharedPreferences(prefs,Context.MODE_PRIVATE).edit()
        editor.putInt("extracost",extracost)
        editor.putBoolean("exisallowed",exchangeenabled)
        editor.apply()

    }
    private fun savetreasury(){
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val editor=settings.edit()
        editor.putInt("goldNum",gold)
        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)


        editor.apply()

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
                val intent= Intent(this,Bankmenu::class.java)
                startActivity(intent)
            } // adding sign out button, which signs out the user if clicked and redirects to Login activity
        }
        return super.onOptionsItemSelected(item)


    }
}
