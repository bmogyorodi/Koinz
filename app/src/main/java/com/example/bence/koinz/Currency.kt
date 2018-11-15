package com.example.bence.koinz

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_currency.*

class Currency : AppCompatActivity() {

    private var gold=0
    private var peny=0
    private var quid=0
    private var shil=0
    private var dolr=0
    private var penyx=0
    private var quidx=0
    private var shilx=0
    private var dolrx=0
    private var penysel=0
    private var quidsel=0
    private var shilsel=0
    private var dolrsel=0
    private var addedgold=0
    // currency names are the amount the player has, curencyx is the exchange rate between gold and currency, currencysel is the amount from that currency selected for conversion
    val prefs= "MyPrefsFile"

    private fun updateaddgold(){
        addedgold=penyx*penysel+dolrx*dolrsel+shilx*shilsel+quidx*quidsel
        goldfromex.text="$addedgold"

    }
    private fun updatetreasury(){
        treasury.text="Gold:$gold Peny:$peny Quid:$quid Shil:$shil Dolr:$dolr"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        updolr.setOnClickListener{_ ->
            if(dolrsel<dolr)
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
            if(penysel<peny){
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
            if (quidsel<quid){
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
            if (shilsel<shil) {
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
            gold=addedgold+gold
            peny=peny-penysel
            quid=quid-quidsel
            shil=shil-shilsel
            dolr=dolr-dolrsel
             penysel=0
             quidsel=0
             shilsel=0
             dolrsel=0
            conpeny.text="0"
            condolr.text="0"
            conquid.text="0"
            conshil.text="0"
            updateaddgold()
            updatetreasury()

        }
        createcur.setOnClickListener{_ ->
            peny=10
            quid=10
            shil=10
            dolr=10
            updatetreasury()

        } // to be deleted


    }

    override fun onStart() {
        super.onStart()
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        gold=settings.getInt("goldNum",0)
        peny=settings.getInt("penyNum",0)
        quid=settings.getInt("quidNum",0)
        shil=settings.getInt("shilNum",0)
        dolr=settings.getInt("dolrNum",0)
        penyx=settings.getInt("penyEx",10)
        quidx=settings.getInt("quidEx",10)
        shilx=settings.getInt("shilEx",10)
        dolrx=settings.getInt("dolrEx",10)
        updatetreasury()
        exrate.text="Changerates: Peny:$penyx Quid:$quidx Shil:$shilx Dolr:$dolrx"

    }

    override fun onStop() {
        super.onStop()
        val settings= getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val editor=settings.edit()
        editor.putInt("goldNum",gold)
        editor.putInt("penyNum",peny)
        editor.putInt("quidNum",quid)
        editor.putInt("shilNum",shil)
        editor.putInt("dolrNum",dolr)

        editor.apply()
    }
}
