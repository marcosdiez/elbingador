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
    fun prepareSmallDeck() : BingoDeck {
        val bingoCard = TestBingoCard.getBingoCard()
        val bingoDeck = BingoDeck()

        bingoDeck.addBingoCard(bingoCard)

      //  return serializeAndDeSerialize(bingoDeck)
        bingoDeck.recalculateNumberMap()
        return bingoDeck
    }

    fun prepareBigDeck() : BingoDeck {
        val bingoDeck = BingoDeck()
        var counter = 0
        for(numDeck in 1 until 11){
            var bingoCard = BingoCard()
            bingoCard.name = String.format("BingoDeck x%03d", numDeck)
            for(row in 0 until bingoCard.content.size){
                for(column in 0 until bingoCard.content[row].size) {
                    bingoCard.content[row][column] = (counter++ % 99) + 1
                }
            }
            bingoDeck.addBingoCard(bingoCard)
        }
        bingoDeck.recalculateNumberMap()
        return bingoDeck
    }

    @Test
    @Throws(Exception::class)
    fun testDeckWithManyCards() {
        val bingoDeck = prepareBigDeck()
        bingoDeck.hit(12)
        bingoDeck.hit(7)
        bingoDeck.hit(22)
        bingoDeck.hit(17)
        bingoDeck.hit(15)
        bingoDeck.hit(27)
        bingoDeck.hit(2)
        System.out.println(bingoDeck)
    }

        @Test
    @Throws(Exception::class)
    fun testDumpState() {
        val bingoDeck = prepareSmallDeck()
        bingoDeck.hit(10)
        bingoDeck.hit(20)
        bingoDeck.hit(30)
        bingoDeck.hit(40)
        bingoDeck.hit(50)
        bingoDeck.hit(60)
        bingoDeck.hit(70)
        System.out.println(bingoDeck.winningCardsToString())

        bingoDeck.hit(39)
        bingoDeck.hit(10)
        bingoDeck.hit(63)
        bingoDeck.hit(86)
        bingoDeck.hit(58)

        System.out.println(bingoDeck.winningCardsToString())


    }

    @Test
    @Throws(Exception::class)
    fun testHitAndUnHit() {
        val bingoDeck = prepareSmallDeck()

        testHitHelper(bingoDeck, initialSize=0, hits=0, number=5)
        testHitHelper(bingoDeck, initialSize=1, hits=1, number=99)
        testHitHelper(bingoDeck, initialSize=2, hits=2, number=98)

        testUnHitHelper(bingoDeck, initialSize=3, hits=0, number=5)
        testUnHitHelper(bingoDeck, initialSize=2, hits=1, number=99)
        testUnHitHelper(bingoDeck, initialSize=1, hits=2, number=98)
    }

    @Test
    @Throws(Exception::class)
    fun testHitAndUnHitBigDeck() {
        val bingoDeck = prepareBigDeck()


        testHitHelper(bingoDeck, initialSize=0, hits=3, number=5)
        testHitHelper(bingoDeck, initialSize=1, hits=2, number=99)
        testHitHelper(bingoDeck, initialSize=2, hits=2, number=98)

        testUnHitHelper(bingoDeck, initialSize=3, hits=3, number=5)
        testUnHitHelper(bingoDeck, initialSize=2, hits=2, number=99)
        testUnHitHelper(bingoDeck, initialSize=1, hits=2, number=98)
    }


    private fun testHitHelper(bingoDeck: BingoDeck, initialSize: Int, hits: Int, number: Int) {
        Assert.assertEquals(initialSize, bingoDeck.numbers.size)
        Assert.assertEquals(hits, bingoDeck.hit(number))
        Assert.assertEquals(initialSize + 1, bingoDeck.numbers.size)
        Assert.assertTrue(bingoDeck.numbers.contains(number))
    }

    private fun testUnHitHelper(bingoDeck: BingoDeck, initialSize: Int, hits: Int, number: Int) {
        Assert.assertEquals(initialSize, bingoDeck.numbers.size)
        Assert.assertEquals(hits, bingoDeck.unhit(number))
        Assert.assertEquals(initialSize - 1, bingoDeck.numbers.size)
        Assert.assertTrue(!bingoDeck.numbers.contains(number))
    }


    @Test
    @Throws(Exception::class)
    fun testWinninDectection() {
        val bingoDeck = prepareSmallDeck()

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
        val bingoDeck = prepareSmallDeck()

        val serializedBingoDeck = serializeAndDeSerialize(bingoDeck)
        Assert.assertEquals(bingoDeck.getNumbersFromMyBingoCards().size, 24)
        Assert.assertEquals(bingoDeck.getNumbersFromMyBingoCards().size, serializedBingoDeck.getNumbersFromMyBingoCards().size)
    }

    private fun serializeAndDeSerialize(originalBingoDeck: BingoDeck): BingoDeck {
        val bos = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(bos)
        objectOutputStream.writeObject(originalBingoDeck)
        objectOutputStream.flush()
        val serializedObject = bos.toByteArray()
        bos.close()


        val bis = ByteArrayInputStream(serializedObject)
        val objectInputStream = ObjectInputStream(bis)
        val serializedBingoDeck = objectInputStream.readObject() as BingoDeck
        objectInputStream.close()
        return serializedBingoDeck
    }
}
