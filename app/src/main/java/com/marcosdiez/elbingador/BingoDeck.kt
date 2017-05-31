package com.marcosdiez.elbingador

import java.util.*
import kotlin.collections.HashSet

/**
 * Created by Marcos on 2017-05-30.
 */

class BingoDeck : java.io.Serializable {
    val bingoDeck = ArrayList<BingoCard>()
    val winningBingoDeck = ArrayList<BingoCard>()
    private var numberMap = HashMap<Int, HashSet<BingoCard>>()

    fun addBingoCard(bingoCard: BingoCard) {
        bingoDeck.add(bingoCard)
        bingoDeck.sort()

        for (row in 0 until bingoCard.size) {
            for (column in 0 until bingoCard.size) {
                val n = bingoCard.content[row][column]

                if (!numberMap.containsKey(n)) {
                    numberMap[n] = HashSet<BingoCard>()
                }

                val theSet = numberMap[n]
                if (theSet != null) {  // both me and you know it's not null. Kotlin does not, though
                    if (!theSet.contains(bingoCard)) {
                        theSet.add(bingoCard)
                    }
                }
            }
        }
    }



    fun getHits() : Collection<Int> {
        return numberMap.keys
    }

    fun hit(number: Int): Int {
        if (!numberMap.containsKey(number)) {
            return 0
        }

        var hits = 0
        val cardsWithThisNumber = numberMap[number]
        if (cardsWithThisNumber != null) {
            for (bingoCard in cardsWithThisNumber) {
                val hitsFromThisCard = bingoCard.hit(number)
                hits += hitsFromThisCard
                if (hitsFromThisCard > 0) {
                    if (bingoCard.hasWinningAnything()) {
                        if (!winningBingoDeck.contains(bingoCard)) {
                            winningBingoDeck.add(bingoCard)
                        }
                    }
                }
            }
        }
        return hits
    }

    fun unhit(number: Int): Int {
        if (!numberMap.containsKey(number)) {
            return 0
        }

        var hits = 0
        val cardsWithThisNumber = numberMap[number]
        if (cardsWithThisNumber != null) {
            for (bingoCard in cardsWithThisNumber) {
                hits += bingoCard.unhit(number)
                if (winningBingoDeck.contains(bingoCard) && !bingoCard.hasWinningAnything()) {
                    winningBingoDeck.remove(bingoCard)
                }
            }
        }
        return hits
    }
}
