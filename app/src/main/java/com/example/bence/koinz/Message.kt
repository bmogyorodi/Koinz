package com.example.bence.koinz

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Message constructor(private val id:String,private val fromid:String,private val toid:String,private val coin: Coinz) {
    constructor():this("","","",coin=Coinz())






}