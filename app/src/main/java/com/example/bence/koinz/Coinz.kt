package com.example.bence.koinz




class Coinz constructor(id:String,value:Float,currency:String,markersym:Int,markercolor:String,latitude:Float,longitude:Float,taken:Boolean){

    private val id=id
    private val value =value
    private val currency=currency
    private val markersym=markersym
    private val markercolor=markercolor
    private val latitude=latitude
    private val longitude=longitude
    private var taken=taken




    constructor():this("", 0F,"",0,"",0F,0F,true)


    fun getid(): String {
        return this.id.toString()
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









}