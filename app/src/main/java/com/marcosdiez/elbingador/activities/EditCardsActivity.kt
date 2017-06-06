package com.marcosdiez.elbingador.activities

import android.content.Context
import android.content.Intent
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




class EditCardsActivity : AppCompatActivity() {

    private lateinit var numberSet: Set<EditText>
    private lateinit var numberMatrix: ArrayList<ArrayList<EditText>>

    private val FILENAME = "data.raw"

    private lateinit var bingoDeck: BingoDeck
    private lateinit var bingoCardBeingDisplayed: BingoCard
    private var bingoDeckDisplayedIndex = 0

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
            uiToBingoCard()
            val newCard = BingoCard()
            newCard.name = String.format("Cartela x%d", bingoDeck.bingoDeck.size)
            bingoDeck.addBingoCard(newCard)
            bingoCardBeingDisplayed = newCard
            for(i in 0 until bingoDeck.bingoDeck.size){
                if(bingoDeck.bingoDeck[i] == bingoCardBeingDisplayed){
                    bingoDeckDisplayedIndex = i
                    break
                }
            }
            bingoCardToUi(newCard)
            updateUiButtons()
        }

        button_back.setOnClickListener {
            if (bingoDeckDisplayedIndex > 0) {
                changeCardHelper(-1)
            }
        }

        button_next.setOnClickListener {
            if (bingoDeckDisplayedIndex < (bingoDeck.bingoDeck.size - 1)) {
                changeCardHelper(1)
            }
        }

        button_play.setOnClickListener {
            val myIntent = Intent(this, PlayGameActivity::class.java)
            startActivity(myIntent)
        }

    }

    private fun changeCardHelper(delta: Int) {
        uiToBingoCard()
        bingoDeckDisplayedIndex += delta
        bingoCardBeingDisplayed = bingoDeck.bingoDeck[bingoDeckDisplayedIndex]
        bingoCardToUi(bingoCardBeingDisplayed)
        updateUiButtons()
    }

    private fun updateUiButtons() {
        button_back.isEnabled = bingoDeckDisplayedIndex != 0
        button_next.isEnabled = bingoDeckDisplayedIndex != (bingoDeck.bingoDeck.size -1)
    }

    private fun loadDataFromDisk() {
        bingoDeckDisplayedIndex=0
        bingoDeck = loadDataFromDiskHelper()
        if (bingoDeck.bingoDeck.isEmpty()) {
            bingoCardBeingDisplayed = BingoCard()
            bingoCardBeingDisplayed.name = "Cartela 1"
            bingoDeck.addBingoCard(bingoCardBeingDisplayed)
        } else {
            bingoCardBeingDisplayed = bingoDeck.bingoDeck[bingoDeckDisplayedIndex]
        }
        bingoCardToUi(bingoCardBeingDisplayed)
        updateUiButtons()
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
       // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
        bingoCardBeingDisplayed = uiToBingoCard()
        val fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(bingoDeck)
        objectOutputStream.flush()
        fileOutputStream.close()
        log("Data Saved")
    }

    private fun bingoCardToUi(bingoCard: BingoCard) {
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
        text_view_status.text = String.format("Cartela %d de %d", bingoDeckDisplayedIndex + 1, bingoDeck.bingoDeck.size)
    }

    private fun uiToBingoCard(): BingoCard {
        bingoCardBeingDisplayed.name = card_title.text.toString()

        for (row in 0 until numberMatrix.size) {
            for (column in 0 until numberMatrix.size) {
                try {
                    bingoCardBeingDisplayed.content[row][column] = numberMatrix[row][column].text.toString().toInt()
                } catch(nfe: NumberFormatException) {
                    bingoCardBeingDisplayed.content[row][column] = -1
                }
            }
        }
        return bingoCardBeingDisplayed
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
                                        Toast.makeText(this@EditCardsActivity, "Square is still empty", Toast.LENGTH_SHORT).show()
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


