package com.example.bence.koinz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity() {

    private val tag = "Mainactivity"
    private val user = FirebaseAuth.getInstance().currentUser
    private val useruid=user?.uid?:""



    private var downloadDate = ""/* Format: YYYY/MM/DD */


     private val prefsFile = "MyPrefsFile"
     private val coinzFile= "Coinzfile"
     private var daily=25 //amount of coinz user can deposit daily, updated in function daily collect
     private  var wallet=Wallet()
     private var depriator=false
     private var exchangeenabled=true
     private var extracost=100
    private var collectedCoinz=0


    // for storing preferences
     object DownloadCompleteRunner: DownLoadCompleteListener{
        private var result : String? =null
        override fun downloadComplete(result: String) {
            this.result = result
            
        }
    } // object to download geojson file
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mapmenu.setOnClickListener { _ ->
            val intent = Intent(this, Map::class.java)
            startActivity(intent)

        } //takes user to map activity
        bankmenu.setOnClickListener { _ ->
           val intent = Intent(this,Currency::class.java)
            startActivity(intent)


        } //takes user to bankmenu activity
        bonuses.setOnClickListener{_ ->
            val intent =Intent( this, Bonuses::class.java)
            startActivity(intent)

        } //takes user to bonuses activity
        friendsbutton.setOnClickListener{_ ->
            val intent= Intent( this, Friends::class.java)
            startActivity(intent)
        } // takes user to friends activity






    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {

        super.onStart()
        fetchCurUser()
        val today = LocalDate.now()
        val formatter= DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatted = today.format(formatter)
        val settings= getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        downloadDate=settings.getString("lastDownloadDate","")//getting last download date from prefs file





        if(downloadDate!=""){
            updateTodaysCollection()
        }


        if(user==null){
            val intent= Intent( this, Login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } //takes user to login activity in case there is no user logged in



        if (downloadDate==formatted){

            Log.d(tag,"Files are up to date!")

        }
        else{
            downloadDate=formatted
            downloadgeojson(downloadDate)
            dailycollect()
            dailyconverterdata()
            wallet.getwallet()
            depriator=true

            Log.d(tag,"Files have been updated") // carry out daily updates








        }
        //downloadgeojson(downloadDate)


    }

    override fun onStop() {
        super.onStop()
        Log.d(tag,"[onStop] Storing lastDownloadDate of $downloadDate")
        val settings= getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString("lastDownloadDate",downloadDate)

        editor.apply()
        //save changes to preference files
        if(depriator && wallet.size()>0) {
            Toast.makeText(this,"Quick, deposit your coinz in the bank \n they are losing value every day!",Toast.LENGTH_SHORT)
            wallet.depriciateWallet()
            wallet.savewallet()
        } //depriciating every coin in the wallet by value 1, if the depriator is set to true because it's a new day, and if there is a coin in the wallet

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.signout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,Login::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // adding sign out button, which signs out the user if clicked and redirects to Login activity
        }
        return super.onOptionsItemSelected(item)


    }

    private fun downloadgeojson(todaydate : String){
        val url ="http://homepages.inf.ed.ac.uk/stg/coinz/$todaydate/coinzmap.geojson"

        val download= DownloadFileTask(DownloadCompleteRunner)

        val geo=download.execute(url).get() //async download of Jsonfile, setting url to today


        val json = JSONObject(geo)
        val rates=json.getJSONObject("rates")
        val features=json.getJSONArray("features") //seperate Json file into data on coinz(features) and the rates
        todayrates(rates)
        todaycoinz(features)
        Log.d(tag,"Update completed!")










    }
    private fun dailycollect(){
        val editor=getSharedPreferences(prefsFile,Context.MODE_PRIVATE).edit()
        val settings=getSharedPreferences(prefsFile,Context.MODE_PRIVATE)
        val tier1=settings.getBoolean("dailybonus1",false)
        val tier2=settings.getBoolean("dailybonus2",false)
        val tier3=settings.getBoolean("dailybonus3",false) //examine whether bonuses are activated, update daily collectable coinz accordingly
        if(tier1){
            daily=30
            if(tier2){
                daily=40
                if(tier3){
                    daily=50
                }
            }
        }
        editor.putInt("dailycollect",daily)
        editor.apply()
        Log.d(tag,"Daily collectable coinz set to: $daily")

    }
    private fun todayrates(rates: JSONObject){
        val shil=rates.getString("SHIL").toFloat()
        val dolr=rates.getString("DOLR").toFloat()
        val quid=rates.getString("QUID").toFloat()
        val peny=rates.getString("PENY").toFloat()
        val settings= getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putFloat("shilEX",shil)
        editor.putFloat("dolrEX",dolr)
        editor.putFloat("quidEX",quid)
        editor.putFloat("penyEX",peny)
        editor.apply() // editing daily rates in preference file, Floats taken from JSONObject rates
        Log.d(tag,"Daily rates set: shil $shil, dolr $dolr, quid $quid, peny $peny")
    }
    private fun todaycoinz(features: JSONArray) {
        val settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        val editor=settings.edit()
        for (i in 0..49)
        {
            val feature=features.getJSONObject(i)
            val properties=feature.getJSONObject("properties")
            val geometry=feature.getJSONObject("geometry")
            val id=properties.getString("id")
            val value=properties.getString("value").toFloat()
            val currency=properties.getString("currency")
            val coordinates=geometry.getJSONArray("coordinates")
            val longitude=coordinates.getDouble(0)
            val latitude=coordinates.getDouble(1)
            editor.putString("$i id", id)
            editor.putFloat("$i value",value)
            editor.putString("$i currency",currency)
            editor.putFloat("$i longitude", longitude.toFloat())
            editor.putFloat("$i latitude", latitude.toFloat())
            editor.putBoolean("$i Taken", false) //storing data on coinz on seperate Coinzfile preference file, names include id space attribute



        }
        editor.apply()

    }
    private fun dailyconverterdata(){
        val editor=getSharedPreferences(prefsFile,Context.MODE_PRIVATE).edit()
        editor.putInt("extracost",extracost)
        editor.putBoolean("exisallowed",exchangeenabled)
        editor.apply()

    } // updates values to their default starter values when lastdownload date is updated
    private fun fetchCurUser(){

        val ref = FirebaseDatabase.getInstance().getReference("/users/$useruid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                    Log.d(tag,"User added to the list!, $p0")
                    val user=p0.getValue(User::class.java)!!
                    if(useruid==user.uid){
                        logintag.text="Logged in as: ${user.username}"

                    }





                }
            })

}  //getting current user  to be displayed
    private fun countCollection(){
        val setting=getSharedPreferences(coinzFile, Context.MODE_PRIVATE)
        for (i in 0..49)
        {
            if(setting.getBoolean("$i Taken",false))
            {
                collectedCoinz++
            }
        }
        Log.d(tag,"Today you colleced: $collectedCoinz coinz!")
    } // updates number of collected coinz based on number of isTaken Booleans with value true
    private fun updateTodaysCollection(){
        countCollection()
        val ref=FirebaseDatabase.getInstance().getReference("/collection/$downloadDate/$useruid")
        ref.setValue(collectedCoinz)
    } // rewrites today's collected coinz in the database on the collection/today'sdate/useruid node
}
