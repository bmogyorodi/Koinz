package com.example.bence.koinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_bankmenu.*

class Bankmenu : AppCompatActivity() {

private var gold =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankmenu)
        curconvert.setOnClickListener{_ ->
            val intent = Intent(this, Currency::class.java)
            startActivity(intent)

        }

    }

    override fun onStart() {
        super.onStart()


    }
    override fun onStop(){
        super.onStop()


    }

}
