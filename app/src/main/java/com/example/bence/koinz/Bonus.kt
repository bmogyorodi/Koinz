package com.example.bence.koinz

import android.widget.Toast

class Bonus constructor(private val cost:Int,private val target:String,private val description:String) {
    private var purchased=false
    fun getcost(): Int {
        return this.cost
    }

    fun gettarger(): String {
        return this.target
    }

    fun getdescription(): String {
        return this.description
    }

    fun ispurchased(): Boolean {
        return this.purchased
    }

    fun setpurchase(boolean: Boolean){
        this.purchased=boolean
    }
}





