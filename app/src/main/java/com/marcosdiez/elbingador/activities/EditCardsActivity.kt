package com.marcosdiez.elbingador.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.marcosdiez.elbingador.BingoCard
import com.marcosdiez.elbingador.BingoDeck
import com.marcosdiez.elbingador.R
import kotlinx.android.synthetic.main.bingo_card.*


class EditCardsActivity : BingadorBasicActivity() {

    private lateinit var numberSet: Set<EditText>
    private lateinit var numberMatrix: ArrayList<ArrayList<EditText>>

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
            newCard.name = makeBingoCardName(bingoDeck.bingoDeck.size + 1)
            bingoDeck.addBingoCard(newCard)
            bingoCardBeingDisplayed = newCard
            for (i in 0 until bingoDeck.bingoDeck.size) {
                if (bingoDeck.bingoDeck[i] == bingoCardBeingDisplayed) {
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

        button_fill_random.setOnClickListener {
            for (n in numberSet) {
                if (n.text.toString() == "") {
                    n.setText(((Math.round(Math.random() * 10000).toInt() % 98) + 1).toString())
                }
            }
        }


        button_erase_all_cards.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Apagar Tudo")
            alert.setMessage("Você tem certeza que quer apagar todas as cartelas e números ?")
            alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->
                bingoDeck = BingoDeck()
                onResumeStep2()
                dialog.dismiss()
            })

            alert.setNegativeButton("Não", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

            alert.show()

        }


    }

    private fun changeCardHelper(delta: Int) {
        bingoCardBeingDisplayed = uiToBingoCard()
        bingoDeckDisplayedIndex += delta
        bingoCardBeingDisplayed = bingoDeck.bingoDeck[bingoDeckDisplayedIndex]
        bingoCardToUi(bingoCardBeingDisplayed)
        updateUiButtons()

    }

    private fun updateUiButtons() {
        button_back.isEnabled = bingoDeckDisplayedIndex != 0
        button_next.isEnabled = bingoDeckDisplayedIndex != (bingoDeck.bingoDeck.size - 1)
    }

    private fun makeBingoCardName(n: Int) : String {
        return String.format("Cartela x%03d", n)
    }

    private fun prepareBingoCardToBeDisplayed() {
        bingoDeckDisplayedIndex = 0
        if (bingoDeck.bingoDeck.isEmpty()) {
            bingoCardBeingDisplayed = BingoCard()
            bingoCardBeingDisplayed.name = makeBingoCardName(1)
            bingoDeck.addBingoCard(bingoCardBeingDisplayed)
        } else {
            bingoCardBeingDisplayed = bingoDeck.bingoDeck[bingoDeckDisplayedIndex]
        }
    }

    override fun onPause() {
        bingoCardBeingDisplayed = uiToBingoCard()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        onResumeStep2()
    }

    private fun onResumeStep2() {
        prepareBingoCardToBeDisplayed()
        bingoCardToUi(bingoCardBeingDisplayed)
        updateUiButtons()
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


