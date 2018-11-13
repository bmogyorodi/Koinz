package com.example.bence.koinz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val tag = "Mainactivity"

    private var downloadDate = "" // Format: YYYY/MM/DD
    private val preferencesFile = "MyPrefsFile" // for storing preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mapmenu.setOnClickListener { _ ->
            val intent = Intent(this, Map::class.java)
            startActivity(intent)

        }
        bankmenu.setOnClickListener { _ ->
           val intent = Intent(this,Bankmenu::class.java)
            startActivity(intent)

        }
        bonuses.setOnClickListener{_ ->
            val intent =Intent( this, Bonuses::class.java)
            startActivity(intent)

        }
        friendsbutton.setOnClickListener{_ ->
            val intent= Intent( this, Friends::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {

        super.onStart()
        val settings= getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
        downloadDate=settings.getString("lastDownloadDate","")
        Log.d(tag,"[onStart] Recalled lastDownloadDate is `$downloadDate`")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag,"[onStop] Storing lastDownloadDate of $downloadDate")
        val settings= getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
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
