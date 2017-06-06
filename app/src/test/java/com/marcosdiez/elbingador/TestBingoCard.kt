package com.marcosdiez.elbingador

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class TestBingoCard {

    companion object StaticStuff {

        fun getBingoCard(): BingoCard {
            /*
             78   98   76   44   73
             98   48   99   18   68
             81   26   50  [ 1]  71
            [49]  32   20   92   96
             39  [10]  63   86   58
         */
            val bingoCard = BingoCard()

            bingoCard.content[0][0] = 78
            bingoCard.content[0][1] = 98   // this appears twice, by design
            bingoCard.content[0][2] = 76
            bingoCard.content[0][3] = 44
            bingoCard.content[0][4] = 73
            bingoCard.content[1][0] = 98
            bingoCard.content[1][1] = 48
            bingoCard.content[1][2] = 99
            bingoCard.content[1][3] = 18
            bingoCard.content[1][4] = 68
            bingoCard.content[2][0] = 81
            bingoCard.content[2][1] = 26
            bingoCard.content[2][2] = 50
            bingoCard.content[2][3] = 1
            bingoCard.content[2][4] = 71
            bingoCard.content[3][0] = 49
            bingoCard.content[3][1] = 32
            bingoCard.content[3][2] = 20
            bingoCard.content[3][3] = 92
            bingoCard.content[3][4] = 96
            bingoCard.content[4][0] = 39
            bingoCard.content[4][1] = 10
            bingoCard.content[4][2] = 63
            bingoCard.content[4][3] = 86
            bingoCard.content[4][4] = 58

//        for (row in 0 until bingoCard.size) {
//            for (column in 0 until bingoCard.size) {
//                val n = (Math.abs(generator.nextInt()) % 99) + 1
//                System.out.println(String.format("bingoCard.content[%d][%d] = %d", row, column , n))
//                bingoCard.content[row][column] = n
//            }
//        }
            return bingoCard
        }

    }
//    private val generator = Random(42)


    @Test
    @Throws(Exception::class)
    fun createBingoCard() {
        val bingoCard = getBingoCard()
        bingoCard.hits[2][3] = true
        bingoCard.hits[4][1] = true
        bingoCard.hits[3][0] = true

        System.out.print(bingoCard)
        assertFalse(bingoCard.hasWinningAnything())
    }

    @Test
    @Throws(Exception::class)
    fun testWinningRow() {
        val bingoCard = getBingoCard()
        for (n in intArrayOf(98, 48, 99, 18, 68)) {
            bingoCard.hit(n)
        }
        assertTrue(bingoCard.hasWinningRow())
        assertFalse(bingoCard.hasWinningColumn())
        assertFalse(bingoCard.hasWinningFirstDiagonal())
        assertFalse(bingoCard.hasWinningSecondDiagonal())
        assertTrue(bingoCard.hasWinningAnything())
    }

    @Test
    @Throws(Exception::class)
    fun testWinningColumn() {
        val bingoCard = getBingoCard()
        for (n in intArrayOf(98, 48, 26, 32, 10)) {
            bingoCard.hit(n)
        }
        assertFalse(bingoCard.hasWinningRow())
        assertTrue(bingoCard.hasWinningColumn())
        assertFalse(bingoCard.hasWinningFirstDiagonal())
        assertFalse(bingoCard.hasWinningSecondDiagonal())
        assertTrue(bingoCard.hasWinningAnything())
    }

    @Test
    @Throws(Exception::class)
    fun testWinningFirstDiagonal() {
        val bingoCard = getBingoCard()
        for (n in intArrayOf(78, 48, 50, 92, 58)) {
            bingoCard.hit(n)
        }
        assertFalse(bingoCard.hasWinningRow())
        assertFalse(bingoCard.hasWinningColumn())
        assertTrue(bingoCard.hasWinningFirstDiagonal())
        assertFalse(bingoCard.hasWinningSecondDiagonal())
        assertTrue(bingoCard.hasWinningAnything())
    }

    @Test
    @Throws(Exception::class)
    fun testWinningSecondDiagonal() {
        val bingoCard = getBingoCard()
        for (n in intArrayOf(73, 18, 50, 32, 39)) {
            bingoCard.hit(n)
        }
        assertFalse(bingoCard.hasWinningRow())
        assertFalse(bingoCard.hasWinningColumn())
        assertFalse(bingoCard.hasWinningFirstDiagonal())
        assertTrue(bingoCard.hasWinningSecondDiagonal())
        assertTrue(bingoCard.hasWinningAnything())
    }

    @Test
    @Throws(Exception::class)
    fun testHitAndUnhit() {
        val bingoCard = getBingoCard()
        assertEquals(0, bingoCard.numHits)

        bingoCard.hit(42)
        assertEquals(0, bingoCard.numHits)

        bingoCard.hit(99)
        assertEquals(1, bingoCard.numHits)

        bingoCard.hit(18)
        assertEquals(2, bingoCard.numHits)

        bingoCard.hit(18)
        assertEquals(2, bingoCard.numHits)

        bingoCard.hit(98)
        assertEquals(4, bingoCard.numHits)

        bingoCard.hit(98)
        assertEquals(4, bingoCard.numHits)

        bingoCard.unhit(33)
        assertEquals(4, bingoCard.numHits)

        bingoCard.unhit(50)
        assertEquals(4, bingoCard.numHits)

        bingoCard.unhit(99)
        assertEquals(3, bingoCard.numHits)

        bingoCard.unhit(98)
        bingoCard.unhit(98)
        bingoCard.unhit(98)
        bingoCard.unhit(98)
        assertEquals(1, bingoCard.numHits)
    }


    @Test
    @Throws(Exception::class)
    fun testSerialize() {
        val bingoCard = getBingoCard()

        val bos = ByteArrayOutputStream()
        val out = ObjectOutputStream(bos)
        out.writeObject(bingoCard)
        out.flush()
        val serializedObject = bos.toByteArray()
        bos.close()


        val bis = ByteArrayInputStream(serializedObject)
        val zin = ObjectInputStream(bis)
        val serializedBingoCard = zin.readObject() as BingoCard
        zin.close()

        assertEquals(bingoCard.name, serializedBingoCard.name)
    }

}