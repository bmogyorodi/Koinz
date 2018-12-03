package com.example.bence.koinz

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Message constructor( val id:String, val fromid: String,val fromname: String, val toid: String, val coin: Coinz) {
    constructor():this("","","","",coin=Coinz())

    override fun equals(other: Any?): Boolean {
        if (other is Message){
            this.id==other.id
        }
        return false
    }









}
class FriendRequest constructor(val id: String,val fromid: String,val fromname:String,val toid: String){
    constructor():this("","","","")

    override fun equals(other: Any?): Boolean {
        if(other is FriendRequest){
            this.id==other.id
        }
        return false
    }
}
class ChallengeRequest constructor(var id:String,val fromid: String,val fromname: String,val ondate:String,val toid: String,val toname:String){
    constructor():this("","","","","","")
    fun resetid(newid:String){
        this.id=newid
    }

    override fun equals(other: Any?): Boolean {
        if(other is ChallengeRequest){
            this.id==other.id
        }
        return false
    }
}