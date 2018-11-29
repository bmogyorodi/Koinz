package com.example.bence.koinz


import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_bonuses.*

class Bonuses : AppCompatActivity() {
    private var radbonustier1=Bonus(800,"radius","Collect Coinz from 40 meters(cost 800G)")
    private var radbonustier2=Bonus(1000,"radius","Collect Coinz from 65 meters(cost 1000G)")
    private var radbonustier3=Bonus(1500,"radius","Collect Coinz from 100 meters(cost 1500G)")
    private var dailybonustier1=Bonus(500,"dailycoinz","Collect 30 coinz daily (cost 500G)")
    private var dailybonustier2=Bonus(750,"dailycoinz","Collect 40 coinz daily (cost 750G)")
    private var dailybonustier3=Bonus(750,"dailycoinz","Collect all daily (cost 1000G)")
    private val bonusprefs="MyPrefsFile"
    private var gold=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonuses)
        supportActionBar?.title="Bonuses"











    }

    override fun onStart() {
        super.onStart()
        val settings=getSharedPreferences(bonusprefs,Context.MODE_PRIVATE)
        radbonustier1.setpurchase(settings.getBoolean("radius1",false))
        radbonustier2.setpurchase(settings.getBoolean("radius2",false))
        radbonustier3.setpurchase(settings.getBoolean("radius3",false))
        dailybonustier1.setpurchase(settings.getBoolean("dailybonus1",false))
        dailybonustier2.setpurchase(settings.getBoolean("dailybonus2",false))
        dailybonustier3.setpurchase(settings.getBoolean("dailybonus3",false))
        gold=settings.getInt("goldNum",0)
        updateBonusButtons()




    }
    private fun updateBonusButtons(){
        if(radbonustier1.ispurchased()){
            buttonradius2.isEnabled=true
            buttonradius2.text=radbonustier2.getdescription()
            if (radbonustier2.ispurchased())
            {
                buttonradius3.isEnabled=true
                buttonradius3.text=radbonustier3.getdescription()
            }
        }
        if(dailybonustier1.ispurchased())
        {
            buttondaily2.isEnabled=true
            buttondaily2.text=dailybonustier2.getdescription()
            if(dailybonustier2.ispurchased())
            {
                buttondaily3.isEnabled=true
                buttondaily3.text=dailybonustier3.getdescription()
            }
        }

    }
}






