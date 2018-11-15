package com.example.bence.koinz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.mapbox.geojson.FeatureCollection
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    private val tag = "Mainactivity"

    private var downloadDate = "" /* Format: YYYY/MM/DD */


     val prefsFile = "MyPrefsFile"// for storing preferences
     object DownloadCompleteRunner: DownLoadCompleteListener{
        var result : String? =null
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
            val intent= Intent( this, Friends::class.java)
            startActivity(intent)
        } // takes user to friends activity






    }

    override fun onStart() {

        super.onStart()

        val today = LocalDate.now()
        val formatter= DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatted = today.format(formatter)
        val settings= getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        downloadDate=settings.getString("lastDownloadDate","") //getting last download date from prefs file
        if (downloadDate==formatted){
            datetag.text = "Files up to date!"
        }
        else{
            downloadDate=formatted
            val url ="http://homepages.inf.ed.ac.uk/stg/coinz/$formatted/coinzmap.geojson"

            val download= DownloadFileTask(DownloadCompleteRunner)
            val geo=download.execute(url).get()
            var geoj=FeatureCollection.fromJson(geo).features()
            geoj?.get(1)?.getStringProperty("id")








        }
        val url ="http://homepages.inf.ed.ac.uk/stg/coinz/$formatted/coinzmap.geojson"

        val download= DownloadFileTask(DownloadCompleteRunner) //to be deleted once geojson reader installed
        download.execute(url) //to be deleted once geojson reader installed
        val geo=DownloadCompleteRunner.result //to be deleted once geojson reader installed
        datetag.text = "Last update: $downloadDate"
        Log.d(tag,"[onStart] Recalled lastDownloadDate is `$downloadDate`")
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
}
