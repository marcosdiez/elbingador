package com.marcosdiez.elbingador

/**
 * Created by Marcos on 2017-05-31.
 */



import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class TestBingoDeck {

    /*
 78   98   76   44   73
 98   48   99   18   68
 81   26   50  [ 1]  71
[49]  32   20   92   96
 39  [10]  63   86   58
*/
    fun prepareDeck() : BingoDeck {
        val bingoCard = TestBingoCard.getBingoCard()
        val bingoDeck = BingoDeck()

        bingoDeck.addBingoCard(bingoCard)
        return bingoDeck
    }


    @Test
    @Throws(Exception::class)
    fun testHitAndUnHit() {
        val bingoDeck = prepareDeck()

        Assert.assertEquals(0, bingoDeck.hit(5))
        Assert.assertEquals(1, bingoDeck.hit(99))
        Assert.assertEquals(2, bingoDeck.hit(98))

        Assert.assertEquals(0, bingoDeck.unhit(5))
        Assert.assertEquals(1, bingoDeck.unhit(99))
        Assert.assertEquals(2, bingoDeck.unhit(98))
    }


    @Test
    @Throws(Exception::class)
    fun testWinninDectection() {
        val bingoDeck = prepareDeck()

        Assert.assertEquals(0, bingoDeck.hit(5))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)
        Assert.assertEquals(1, bingoDeck.hit(99))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)
        Assert.assertEquals(2, bingoDeck.hit(98))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)
        Assert.assertEquals(1, bingoDeck.hit(48))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)
        Assert.assertEquals(1, bingoDeck.hit(18))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)
        Assert.assertEquals(1, bingoDeck.hit(68))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.unhit(68))
        Assert.assertEquals(0, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.hit(68))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.hit(73))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.hit(71))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.hit(96))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.hit(58))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

        Assert.assertEquals(1, bingoDeck.unhit(71))
        Assert.assertEquals(1, bingoDeck.winningBingoDeck.size)

    }

    @Test
    @Throws(Exception::class)
    fun testSerialize() {
        val bingoDeck = prepareDeck()

        val bos = ByteArrayOutputStream()
        val out = ObjectOutputStream(bos)
        out.writeObject(bingoDeck)
        out.flush()
        val serializedObject = bos.toByteArray()
        bos.close()


        val bis = ByteArrayInputStream(serializedObject)
        val zin = ObjectInputStream(bis)
        val serializedBingoDeck = zin.readObject() as BingoDeck
        zin.close()

        Assert.assertEquals(bingoDeck.getHits().size, serializedBingoDeck.getHits().size)
    }
}
