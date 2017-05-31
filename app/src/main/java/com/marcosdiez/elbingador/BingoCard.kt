package com.marcosdiez.elbingador

/**
 * Created by Marcos on 2017-05-30.
 */
class BingoCard constructor(val size: Int, var name: String){
    val content = array2dOfInt(size, size)
    val hits = array2dOfBoolean(size, size)
    var numHits = 0

    fun hit(number: Int){
        for(row in 0 until size) {
            for (column in 0 until size) {
                if(content[row][column] == number){
                    if(!hits[row][column]){
                        hits[row][column] = true
                        numHits++
                    }
                }
            }
        }
    }

    fun unhit(number: Int){
        for(row in 0 until size) {
            for (column in 0 until size) {
                if(content[row][column] == number){
                    if(hits[row][column]){
                        hits[row][column] = false
                        numHits--
                    }
                }
            }
        }
    }

    fun hasWinningRow() : Boolean {
        for(row in 0 until size) {
            var winning = true
            for (column in 0 until size) {
                winning = winning && hits[row][column]
                if(!winning){
                    break
                }
            }
            if(winning){
                return true
            }
        }
        return false
    }

    fun hasWinningColumn() : Boolean {
        for(column in 0 until size) {
            var winning = true
            for (row in 0 until size) {
                winning = winning && hits[row][column]
                if(!winning){
                    break
                }
            }
            if(winning){
                return true
            }
        }
        return false
    }

    fun hasWinningFirstDiagonal() : Boolean {
        var winning = true
        for(rowAndColumn in 0 until size){
            winning = winning && hits[rowAndColumn][rowAndColumn]
            if(!winning){
                break
            }
        }
        if(winning){
            return true
        }
        return false
    }

    fun hasWinningSecondDiagonal() : Boolean {
        var winning = true
        for(rowAndColumn in 0 until size){
            winning = winning && hits[size - rowAndColumn - 1][rowAndColumn]
            if(!winning){
                break
            }
        }
        if(winning){
            return true
        }
        return false
    }

    fun hasWinningFullCard(): Boolean {
        for(row in 0 until size) {
            for (column in 0 until size) {
                if(!hits[row][column]){
                    return false
                }
            }
        }
        return true
    }

    fun hasWinningAnything(): Boolean {
        return hasWinningRow() || hasWinningColumn() || hasWinningFirstDiagonal() || hasWinningSecondDiagonal() || hasWinningFullCard()
    }



    fun print(){
        val formaHit = "[%2d] "
        val formaNotHit = " %2d  "

        System.out.println(name)
        for(row in 0 until size){
            for(column in 0 until size){
                val forma = if (hits[row][column]) formaHit else formaNotHit
                System.out.print(String.format(forma, content[row][column]))
            }
            System.out.println()
        }
        System.out.println()
    }



    public inline fun <reified INNER> array2d(sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int)->INNER): Array<Array<INNER>>
            = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

    public fun array2dOfInt(sizeOuter: Int, sizeInner: Int): Array<IntArray>
            = Array(sizeOuter) { IntArray(sizeInner) }

    public fun array2dOfLong(sizeOuter: Int, sizeInner: Int): Array<LongArray>
            = Array(sizeOuter) { LongArray(sizeInner) }

    public fun array2dOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray>
            = Array(sizeOuter) { ByteArray(sizeInner) }

    public fun array2dOfChar(sizeOuter: Int, sizeInner: Int): Array<CharArray>
            = Array(sizeOuter) { CharArray(sizeInner) }

    public fun array2dOfBoolean(sizeOuter: Int, sizeInner: Int): Array<BooleanArray>
            = Array(sizeOuter) { BooleanArray(sizeInner) }


}