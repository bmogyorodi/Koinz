package com.example.bence.koinz




class Coinz constructor(private val id: String, private var value: Float, private val currency: String,   private val latitude: Float, private val longitude: Float, private var taken: Boolean){


    constructor():this("", 0F,"",0F,0F,true)
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
    //getter functions
    fun depriciate(){
        this.value -= 1
        if(this.value<0)
        {
            this.value=0F
        }
    } // reduces the value of the coin by one (coinz in the wallet needs to lose 1 from their value
    override fun equals(other: Any?): Boolean {
        if(other is Coinz){
            return this.getid()==other.getid()
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + taken.hashCode()
        return result
    }


}