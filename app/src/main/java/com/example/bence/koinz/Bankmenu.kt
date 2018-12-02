package com.example.bence.koinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bankmenu.*

class Bankmenu : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title="Bankmenu"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankmenu)
        curconvert.setOnClickListener{_ ->
            val intent = Intent(this, Currency::class.java)
            startActivity(intent)

        }
        coinmelt.setOnClickListener{_ ->
            val intent = Intent(this, Depositcoinz::class.java)
            startActivity(intent)

        }

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
                val intent=Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // adding sign out button, which signs out the user if clicked and redirects to Login activity
        }
        return super.onOptionsItemSelected(item)


    }



}
