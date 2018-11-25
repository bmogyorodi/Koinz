package com.example.bence.koinz

import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point

class Coinz constructor(id:String,value:Float,currency:String,markersym:Int,markercolor:String,latitude:Float,longitude:Float){

    private val id=id
    private val value =value
    private val currency=currency
    private val markersym=markersym
    private val markercolor=markercolor
    private val latitude=latitude
    private val longitude=longitude


     fun getid(): String {
        return this.id.toString()
    }
     fun getvalue(): Float{
        return this.value.toString().toFloat()
    }
     fun getcurrency():String{
        return this.currency.toString()
    }
     fun getmarkersym():Int{
        return this.markersym.toString().toInt()
    }
     fun getmarkercolor():String{
        return this.markercolor.toString()
    }
     fun getlat(): Double{
        return this.latitude.toDouble()
    }
     fun getlong(): Double{
        return this.longitude.toDouble()
    }







}