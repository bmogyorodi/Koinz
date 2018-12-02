package com.example.bence.koinz




class Coinz constructor(private val id: String, private var value: Float, private val currency: String, private val markersym: Int, private val markercolor: String, private val latitude: Float, private val longitude: Float, private var taken: Boolean){


    constructor():this("", 0F,"",0,"",0F,0F,true)
    // this constructor is made to enable fetching the coinz from the database in the wallet class


    fun getid(): String {
        return this.id
    }
     fun getvalue(): Float{
        return this.value.toString().toFloat()
    }
     fun getcurrency():String{
        return this.currency
    }
     fun getmarkersym():Int{
        return this.markersym.toString().toInt()
    }
     fun getmarkercolor():String{
        return this.markercolor
    }
     fun getlat(): Double{
        return this.latitude.toDouble()
    }
     fun getlong(): Double{
        return this.longitude.toDouble()
    }
    fun istaken(): Boolean{
        return this.taken
    }
    fun taken(){
        this.taken=true
    }
    fun depriciate(){
        this.value -= 1
        if(this.value<0)
        {
            this.value=0F
        }
    }
    override fun equals(other: Any?): Boolean {
        if(other is Coinz){
            return this.getid()==other.getid()
        }
        return false
    }









}