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

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity() {

    private val tag = "Mainactivity"


    private var downloadDate = ""/* Format: YYYY/MM/DD */


     private val prefsFile = "MyPrefsFile"
     private val coinzFile= "Coinzfile"
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
           val intent = Intent(this,Bankmenu::class.java)
            startActivity(intent)


        } //takes user to bankmenu activity
        bonuses.setOnClickListener{_ ->
            val intent =Intent( this, Bonuses::class.java)
            startActivity(intent)

        } //takes user to bonuses activity
        friendsbutton.setOnClickListener{_ ->
            val intent= Intent( this, Login::class.java)
            startActivity(intent)
        } // takes user to friends activity






    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {

        super.onStart()

        val today = LocalDate.now()
        val formatter= DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatted = today.format(formatter)
        val settings= getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        downloadDate=settings.getString("lastDownloadDate","")//getting last download date from prefs file/* get last downloaded Jsonfile */

        if (downloadDate==formatted){
            datetag.text = "Files are up to date!"
            Log.d(tag,"Files are up to date!")

        }
        else{
            downloadDate=formatted
            downloadgeojson(downloadDate)
            datetag.text = "Files have been updated!"
            Log.d(tag,"Files have been updated")








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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun downloadgeojson(todaydate : String){
        val url ="http://homepages.inf.ed.ac.uk/stg/coinz/$todaydate/coinzmap.geojson"

        val download= DownloadFileTask(DownloadCompleteRunner)

        val geo=download.execute(url).get()


        val json = JSONObject(geo)
        val rates=json.getJSONObject("rates")
        val features=json.getJSONArray("features")
        todayrates(rates)
        todaycoinz(features)
        Toast.makeText(this,"Update completed!",Toast.LENGTH_SHORT)










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
        editor.apply()
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
            val markersym=properties.getString("marker-symbol").toInt()
            val coordinates=geometry.getJSONArray("coordinates")
            val markercolor=properties.getString("marker-color")
            val longitude=coordinates.getDouble(0)
            val latitude=coordinates.getDouble(1)
            editor.putString("$i id", id)
            editor.putFloat("$i value",value)
            editor.putString("$i currency",currency)
            editor.putInt("$i markersym",markersym)
            editor.putString("$i markercolor",markercolor)
            editor.putFloat("$i longitude", longitude.toFloat())
            editor.putFloat("$i latitude", latitude.toFloat())
            editor.putBoolean("$i Taken", false)



        }
        editor.apply()

    }

}
