package com.example.bence.koinz

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Message constructor( val id:String, val fromid:String, val toid:String, val coin: Coinz) {
    constructor():this("","","",coin=Coinz())









}