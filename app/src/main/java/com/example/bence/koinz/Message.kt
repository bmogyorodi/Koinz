package com.example.bence.koinz



class Message constructor( val id:String, val fromid: String,val fromname: String, val toid: String, val coin: Coinz) {
    constructor():this("","","","",coin=Coinz())

    override fun equals(other: Any?): Boolean {
        if (other is Message){
           return this.id==other.id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + fromid.hashCode()
        result = 31 * result + fromname.hashCode()
        result = 31 * result + toid.hashCode()
        result = 31 * result + coin.hashCode()
        return result
    }


} //data structure used for messages or sent coinz. Stores the id of the node where the data is stored, stores the id and name of sender and receiver
class FriendRequest constructor(val id: String,val fromid: String,val fromname:String,val toid: String){
    constructor():this("","","","")

    override fun equals(other: Any?): Boolean {
        if(other is FriendRequest){
          return  this.id==other.id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + fromid.hashCode()
        result = 31 * result + fromname.hashCode()
        result = 31 * result + toid.hashCode()
        return result
    }
}
class ChallengeRequest constructor(var id:String,val fromid: String,val fromname: String,val ondate:String,val toid: String,val toname:String){
    constructor():this("","","","","","")
    fun resetid(newid:String){
        this.id=newid
    }

    override fun equals(other: Any?): Boolean {
        if(other is ChallengeRequest){
            return this.id==other.id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + fromid.hashCode()
        result = 31 * result + fromname.hashCode()
        result = 31 * result + ondate.hashCode()
        result = 31 * result + toid.hashCode()
        result = 31 * result + toname.hashCode()
        return result
    }
}