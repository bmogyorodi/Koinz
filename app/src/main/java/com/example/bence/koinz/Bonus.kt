package com.example.bence.koinz


class Bonus constructor(private val cost:Int,private val target:String,private val description:String,private val onComplete:String) {
    //seperate class containing data about a bonus, making it easier if they are bunched in the same class
    private var purchased=false
    fun getcost(): Int {
        return this.cost
        //cost of the bonus
    }

    fun gettarger(): String {
        return this.target
        // name of the place in the myprefsfile which stores the bonus ispurchased attribute
    }
    fun getOncomplete():String{
        return this.onComplete
        // text to print/display when bonus is purchased
    }

    fun getdescription(): String {
        return this.description
        // text to print/display to see purpuse/effect of the bonus
    }

    fun ispurchased(): Boolean {
        return this.purchased
        // shows whether the bonus is purchased set to false on default
    }

    fun setpurchase(boolean: Boolean){
        this.purchased=boolean
        // setter of purchased boolean
    }
}





