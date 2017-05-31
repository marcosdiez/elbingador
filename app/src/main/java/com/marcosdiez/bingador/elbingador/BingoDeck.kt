package com.marcosdiez.bingador.elbingador

import java.util.*
import kotlin.collections.HashSet

/**
 * Created by Marcos on 2017-05-30.
 */

class BingoDeck{

    val bingoDeck = ArrayList<BingoCard>()
    var numberMap = HashMap<Int, HashSet<BingoCard>>()

    fun addBingoCard(bingoCard :BingoCard){
        bingoDeck.add(bingoCard)

        for(row in 0 until bingoCard.size) {
            for (column in 0 until bingoCard.size) {
                val n = bingoCard.content[row][column]

                if(!numberMap.containsKey(n)) {
                    numberMap[n] = HashSet<BingoCard>()
                }

                val theSet = numberMap[n]
                if(theSet!= null){
                    if(theSet.contains(bingoCard)) {
                        theSet.add(bingoCard)
                    }
                }
            }
        }

    }
}
