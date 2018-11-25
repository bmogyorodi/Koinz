package com.example.bence.koinz

class Wallet {
    var coin = mutableListOf<Int>()
    var cur = mutableListOf<String>()

    fun addCoin(coinz: Coinz){
        if(coin.size<100) {
            coin.add(coinz.getmarkersym())
            cur.add(coinz.getcurrency())
        }

    }
    fun removeCoin(index: Int){
        if(index<this.coin.size)
        {
            coin.removeAt(index)
            cur.removeAt(index)
        }
        fun getcoinvalue(index: Int): Int {
            return coin.get(index)
        }
        fun getcoincurrency(index: Int): String {
            return cur.get(index)
        }
    }
}