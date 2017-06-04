package com.marcosdiez.elbingador.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.marcosdiez.elbingador.BingoCard
import com.marcosdiez.elbingador.BingoDeck
import com.marcosdiez.elbingador.R
import kotlinx.android.synthetic.main.bingo_card.*
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class Main2Activity : AppCompatActivity() {

    private lateinit var numberSet: Set<EditText>
    private lateinit var numberMatrix: ArrayList<ArrayList<EditText>>

    private val FILENAME = "data.raw"

    private lateinit var bingoDeck: BingoDeck
    private lateinit var bingoCardbeingDisplayed: BingoCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bingo_card)

        initializeCollections()

        fixKeyboardNextButton(numberSet)
        number00.requestFocus()

        button_clear_cells.setOnClickListener {
            for (aNumber in numberSet) {
                aNumber.setText("")
            }
            number00.requestFocus()
        }

        button_new.setOnClickListener {
            bingoCardbeingDisplayed = toBingoCard()
            val newCard = BingoCard()
            bingoDeck.addBingoCard(newCard)
            newCard.name = String.format("Cartela %d", bingoDeck.bingoDeck.size)
            bingoCardbeingDisplayed = newCard
            fromBingoCard(newCard)
        }
//
//        button_save.setOnClickListener {
//            //            saveDataToDisk()
//        }
//
//        button_save_and_add.setOnClickListener {
//            //            loadDataFromDisk()
//        }
    }

    private fun loadDataFromDisk() {
        bingoDeck = loadDataFromDiskHelper()
        if (bingoDeck.bingoDeck.isEmpty()) {
            bingoCardbeingDisplayed = BingoCard()
            bingoCardbeingDisplayed.name = "Cartela 1"
            bingoDeck.addBingoCard(bingoCardbeingDisplayed)
        } else {
            bingoCardbeingDisplayed = bingoDeck.bingoDeck[0]
        }
        fromBingoCard(bingoCardbeingDisplayed)

    }

    override fun onPause() {
        saveDataToDisk()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDisk()
    }

    private fun log(msg: String) {
        Log.d(this.localClassName, msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun loadDataFromDiskHelper(): BingoDeck {
        try {
            val fileInputStream = openFileInput(FILENAME)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val serializedBingoDeck = objectInputStream.readObject() as BingoDeck
            objectInputStream.close()
            fileInputStream.close()
            log("Data Loaded")
            return serializedBingoDeck
        } catch(e: FileNotFoundException) {
            return BingoDeck()
        }
    }

    private fun saveDataToDisk() {
        bingoCardbeingDisplayed = toBingoCard()
        val fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(bingoDeck)
        objectOutputStream.flush()
        fileOutputStream.close()
        log("Data Saved")
    }

    private fun fromBingoCard(bingoCard: BingoCard) {
        card_title.setText(bingoCard.name)
        for (row in 0 until numberMatrix.size) {
            for (column in 0 until numberMatrix.size) {
                val nm = numberMatrix[row][column]
                if (bingoCard.content[row][column] != -1) {
                    nm.setText(bingoCard.content[row][column].toString())
                } else {
                    nm.setText("")
                }
                if (bingoCard.hits[row][column]) {
                    nm.setTextColor(Color.RED)
                } else {
                    nm.setTextColor(Color.BLACK)
                }
            }
        }
        text_view_status.text = String.format("Cartela %d de %d", 1, bingoDeck.bingoDeck.size)
    }

    private fun toBingoCard(): BingoCard {
        bingoCardbeingDisplayed.name = card_title.text.toString()

        for (row in 0 until numberMatrix.size) {
            for (column in 0 until numberMatrix.size) {
                try {
                    bingoCardbeingDisplayed.content[row][column] = numberMatrix[row][column].text.toString().toInt()
                } catch(nfe: NumberFormatException) {
                    bingoCardbeingDisplayed.content[row][column] = -1
                }
            }
        }
        return bingoCardbeingDisplayed
    }

    private fun initializeCollections() {
        numberSet = setOf(
                number00, number01, number02, number03, number04,
                number10, number11, number12, number13, number14,
                number20, number21, number22, number23, number24,
                number30, number31, number32, number33, number34,
                number40, number41, number42, number43, number44)

        numberMatrix = ArrayList<ArrayList<EditText>>(
                setOf(
                        ArrayList<EditText>(setOf(number00, number01, number02, number03, number04)),
                        ArrayList<EditText>(setOf(number10, number11, number12, number13, number14)),
                        ArrayList<EditText>(setOf(number20, number21, number22, number23, number24)),
                        ArrayList<EditText>(setOf(number30, number31, number32, number33, number34)),
                        ArrayList<EditText>(setOf(number40, number41, number42, number43, number44))
                )
        )
    }

    private fun fixKeyboardNextButton(numberSet: Set<EditText>) {
        for (aNumber in numberSet) {
            aNumber.setOnEditorActionListener(
                    object : TextView.OnEditorActionListener {
                        override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if (textView != null) {
                                    if (textView.text.isNotEmpty()) {
                                        // there is some data here
                                        return false // so we move to the next square
                                    } else {
                                        Toast.makeText(this@Main2Activity, "Square is still empty", Toast.LENGTH_SHORT).show()
                                        return true   // so we stay in this square
                                    }
                                }
                            }
                            return false
                        }
                    }
            )
        }
    }

}


