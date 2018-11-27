package com.example.bence.koinz

import java.util.ArrayList

class Wallet {
    private var coins = ArrayList<Coinz>(50)


    fun addCoin(coinz: Coinz){
        if(coins.size<50) {
            coins.add(coinz)
        }

    }
    fun removeCoin(coinz: Coinz):Coinz? {
        if(coins.contains(coinz))
        {
            coins.remove(coinz)
            return coinz
        }
        else{return null}
    }
    fun getCoin(index:Int):Coinz?{
        if(!(coins.size>index)){
            return null
        }else{return coins.get(index)}
    }
    fun size():Int{
      return coins.size
    }
    fun savewallet(){
        //save wallet data for future use
    }
    fun getwallet(){
        //to get wallet from save
    }
}