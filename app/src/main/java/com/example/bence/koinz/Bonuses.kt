package com.example.bence.koinz


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bonuses.*

class Bonuses : AppCompatActivity() {
    private var radbonustier1=Bonus(800,"radius1","Collect Coinz from 40 meters(cost 800G)","Collector radius upgraded to 40 meters!")
    private var radbonustier2=Bonus(1000,"radius2","Collect Coinz from 65 meters(cost 1000G)","Collector radius upgraded to 65 meters!")
    private var radbonustier3=Bonus(1500,"radius3","Collect Coinz from 100 meters(cost 1500G)","Collector radius upgraded to 100 meters!")
    private var dailybonustier1=Bonus(500,"dailybonus1","Collect 30 coinz daily (cost 500G)","Collector upgraded to 30 coinz per day!")
    private var dailybonustier2=Bonus(750,"dailybonus2","Collect 40 coinz daily (cost 750G)","Collector upgraded to 40 coinz per day!")
    private var dailybonustier3=Bonus(750,"dailybonus3","Collect all daily (cost 1000G)","Collector upgraded to max (50 coinz)!")
    //all bonuses set up with attributes
    private lateinit var selectedbonus:Bonus // will take the attributes of the bonus the user selects (based on button push)
    private val bonusprefs="MyPrefsFile" //to take info on what gold the user has, and what bonuses were activated by the user
    private var gold=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonuses)
        supportActionBar?.title="Bonuses"

        buttonradius1.setOnClickListener {_->
            purchaseselector(radbonustier1)

        }
        buttonradius2.setOnClickListener {_->
            purchaseselector(radbonustier2)

        }
        buttonradius3.setOnClickListener {_->
            purchaseselector(radbonustier3)

        }
        buttondaily1.setOnClickListener { _->
            purchaseselector(dailybonustier1)
        }
        buttondaily2.setOnClickListener { _->
            purchaseselector(dailybonustier2)
        }
        buttondaily3.setOnClickListener { _->
            purchaseselector(dailybonustier3)
        }
        //all buttons call purchaseselector function on the corresponding bonus.
        buttonpurchase.setOnClickListener { _->
            purchasebonus()
        } // confirms purchase of selected bonus, with purchase bonus function











    }

    override fun onStart() {
        super.onStart()
        updatepurchasestatus() // update displays base on what gold, and purchased bonuses





    }
    @SuppressLint("SetTextI18n")
    private fun purchasebonus(){
        buttonpurchase.isEnabled=false
        buttonpurchase.text="Confirm purchase"
        display_select_bonus.text="Select Bonus"
        val editor=getSharedPreferences(bonusprefs,Context.MODE_PRIVATE).edit()
        editor.putBoolean(selectedbonus.gettarger(),true)
        editor.putInt("goldNum",gold-selectedbonus.getcost())
        editor.apply() //update prefsfile after purchase, corresponding boolean set to true(purchased) and cost subtracted from gold saved.
        updatepurchasestatus() // update displays base on what gold, and purchased bonuses




    }
    private fun purchaseselector(bonus: Bonus){
        if(gold<bonus.getcost()) //checking whether user has enough gold to purchase
        {
            Toast.makeText(this,"Not enough coinz to purchase this bonus!",Toast.LENGTH_SHORT).show()
        }
        else{
            selectedbonus=bonus
            buttonpurchase.isEnabled=true
            display_select_bonus.text=bonus.getdescription() // set up confirmation button, by enabling and displaying description of selected bonus
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updatepurchasestatus(){
        val settings=getSharedPreferences(bonusprefs,Context.MODE_PRIVATE)
        radbonustier1.setpurchase(settings.getBoolean(radbonustier1.gettarger(),false))
        radbonustier2.setpurchase(settings.getBoolean(radbonustier2.gettarger(),false))
        radbonustier3.setpurchase(settings.getBoolean(radbonustier3.gettarger(),false))
        dailybonustier1.setpurchase(settings.getBoolean(dailybonustier1.gettarger(),false))
        dailybonustier2.setpurchase(settings.getBoolean(dailybonustier2.gettarger(),false))
        dailybonustier3.setpurchase(settings.getBoolean(dailybonustier3.gettarger(),false))
        gold=settings.getInt("goldNum",0)
        displaygold.text="Gold: $gold"
        updateBonusButtons()

    }
    private fun updateBonusButtons() //updates the availability of bonus buttons based on which ones are purchased
    {
        if(radbonustier1.ispurchased()){
            buttonradius1.isEnabled=false
            buttonradius1.text=radbonustier1.getOncomplete()
            buttonradius2.isEnabled=true
            buttonradius2.text=radbonustier2.getdescription()
            if (radbonustier2.ispurchased())
            {
                buttonradius2.isEnabled=false
                buttonradius2.text=radbonustier2.getOncomplete()
                buttonradius3.isEnabled=true
                buttonradius3.text=radbonustier3.getdescription()
                if(radbonustier3.ispurchased()){
                    buttonradius3.isEnabled=false
                    buttonradius3.text=radbonustier3.getOncomplete()
                }
            }
        }
        if(dailybonustier1.ispurchased())
        {
            buttondaily1.isEnabled=false
            buttondaily1.text=dailybonustier1.getOncomplete()
            buttondaily2.isEnabled=true
            buttondaily2.text=dailybonustier2.getdescription()
            if(dailybonustier2.ispurchased())
            {
                buttondaily2.isEnabled=false
                buttondaily2.text=dailybonustier2.getOncomplete()
                buttondaily3.isEnabled=true
                buttondaily3.text=dailybonustier3.getdescription()
                if(dailybonustier3.ispurchased())
                {
                    buttondaily3.isEnabled=false
                    buttondaily3.text=dailybonustier3.getOncomplete()
                }
            }
        }

    }

    // tier 3 can only be purchased in a category if tier 2 is purchased, which can only be purchased if tier 1 is purchased
    //when a tier is unlocked the text changes from locked to the bonus description and button is enabled
    // purchased bonuses display the Oncomplete text which is part of their class
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
                val intent= Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // adding sign out button, which signs out the user if clicked and redirects to Login activity
        }
        return super.onOptionsItemSelected(item)


    }
}






