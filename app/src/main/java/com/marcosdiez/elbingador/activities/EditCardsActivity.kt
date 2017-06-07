package com.marcosdiez.elbingador.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.marcosdiez.elbingador.BingoCard
import com.marcosdiez.elbingador.BingoDeck
import com.marcosdiez.elbingador.R
import kotlinx.android.synthetic.main.bingo_card.*
import java.util.HashMap
import kotlin.collections.ArrayList


data class BingoRestriction(val neighbours: Set<EditText>, val min: Int = 0, val max: Int = 99)


class EditCardsActivity : BingadorBasicActivity() {

    private lateinit var numberSet: Set<EditText>
    private lateinit var numberMatrix: ArrayList<ArrayList<EditText>>
    private val numberRowRules = HashMap<EditText, BingoRestriction>()
    private val previousCell = HashMap<EditText, EditText>()


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

    private fun makeBingoCardName(n: Int): String {
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

        val b = BingoRestriction(setOf(number00, number10, number20, number30, number40), 1, 15)
        numberRowRules[number00] = b
        numberRowRules[number10] = b
        numberRowRules[number20] = b
        numberRowRules[number30] = b
        numberRowRules[number40] = b

        val i = BingoRestriction(setOf(number01, number11, number21, number31, number41), 16, 30)
        numberRowRules[number01] = i
        numberRowRules[number11] = i
        numberRowRules[number21] = i
        numberRowRules[number31] = i
        numberRowRules[number41] = i

        val n = BingoRestriction(setOf(number02, number12, number22, number32, number42), 31, 45)
        numberRowRules[number02] = n
        numberRowRules[number12] = n
        numberRowRules[number22] = n
        numberRowRules[number32] = n
        numberRowRules[number42] = n

        val g = BingoRestriction(setOf(number03, number13, number23, number33, number43), 46, 60)
        numberRowRules[number03] = g
        numberRowRules[number13] = g
        numberRowRules[number23] = g
        numberRowRules[number33] = g
        numberRowRules[number43] = g

        val o = BingoRestriction(setOf(number04, number14, number24, number34, number44), 61, 75)
        numberRowRules[number04] = o
        numberRowRules[number14] = o
        numberRowRules[number24] = o
        numberRowRules[number34] = o
        numberRowRules[number44] = o


        previousCell[number00] = number00
        previousCell[number10] = number00
        previousCell[number20] = number10
        previousCell[number30] = number20
        previousCell[number40] = number30

        previousCell[number01] = number40
        previousCell[number11] = number01
        previousCell[number21] = number11
        previousCell[number31] = number21
        previousCell[number41] = number31

        previousCell[number02] = number41
        previousCell[number12] = number02
        previousCell[number22] = number12
        previousCell[number32] = number22
        previousCell[number42] = number32

        previousCell[number03] = number42
        previousCell[number13] = number03
        previousCell[number23] = number13
        previousCell[number33] = number23
        previousCell[number43] = number33

        previousCell[number04] = number43
        previousCell[number14] = number04
        previousCell[number24] = number14
        previousCell[number34] = number24
        previousCell[number44] = number43
    }

    private fun fixKeyboardNextButton(numberSet: Set<EditText>) {
        for (aNumber in numberSet) {

            aNumber.setOnKeyListener(
                    object : View.OnKeyListener {
                        override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
                            if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_UP) {
                                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
//                            Toast.makeText(this@EditCardsActivity, "keyCode: [" + keyCode + "] keyEvent: [" + keyEvent + "]", Toast.LENGTH_SHORT).show()
                                    if (view is TextView) {
                                        val textView = view
                                        if (textView.text.isNotEmpty()) {
                                            // there is some data here
                                            textView.onEditorAction(EditorInfo.IME_ACTION_NEXT)
                                        } else {
                                            Toast.makeText(this@EditCardsActivity, "O quadrado ainda está vazio!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    return true
                                }
                                if (keyCode in setOf(
                                        KeyEvent.KEYCODE_PLUS,
                                        KeyEvent.KEYCODE_NUMPAD_ADD,
                                        KeyEvent.KEYCODE_NUMPAD_COMMA,
                                        KeyEvent.KEYCODE_NUMPAD_DOT,
                                        KeyEvent.KEYCODE_NUMPAD_SUBTRACT,
                                        KeyEvent.KEYCODE_MINUS,
                                        KeyEvent.KEYCODE_COMMA,
                                        KeyEvent.KEYCODE_PERIOD
                                        )){
                                    System.out.println("KEYCODE IS PLUS or similar")
                                    if(view in previousCell){
                                        System.out.println("requesting focus")
                                        previousCell[view]!!.requestFocus()
                                    }
                                }
                                return true
                            }
                            return false
                        }
                    })

//            aNumber.onFocusChangeListener = object : View.OnFocusChangeListener {
//                override fun onFocusChange(view: View?, hasFocus: Boolean) {
//                    if (hasFocus) {
//                        return
//                    }
//                    if (!checkbox_modo_bebedouro.isChecked) {
//                        return
//                    }
//                    if (view is TextView) {
//                        verifyDataConsistency(view)
//                    }
//                }
//            }

            aNumber.setOnEditorActionListener(
                    object : TextView.OnEditorActionListener {
                        override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
//                        Toast.makeText(this@EditCardsActivity, "actionId: [" + actionId + "] keyEvent: [" + keyEvent + "]", Toast.LENGTH_SHORT).show()
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if (textView != null) {
                                    if (textView.text.isNotEmpty()) {
                                        if(checkbox_modo_bebedouro.isChecked){
                                            val isConsistent = verifyDataConsistency(textView)
                                            if(!isConsistent){
                                                textView.text = ""
                                                return true
                                            }
                                            return false
                                        }
                                        // there is some data here
                                        return false // so we move to the next square
                                    } else {
                                        Toast.makeText(this@EditCardsActivity, "O quadrado ainda está vazio!", Toast.LENGTH_SHORT).show()
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

    private fun verifyDataConsistency(textView: TextView) : Boolean {
        val neighbours = numberRowRules[textView]
        if (neighbours == null) {
            return true
        }
        val textFromThisView = textView.text.toString()
        if (textFromThisView.isEmpty()) {
            return true
        }

        try {
            val intValue = Integer.parseInt(textFromThisView)
            if (textView == number22 && intValue == 0) {   // zero is accepted in the middle row
                return true
            }
            if (intValue > neighbours.max) {
                Toast.makeText(this@EditCardsActivity, "Erro: O número [" + textView.text + "] é maior que [" + neighbours.max + "]", Toast.LENGTH_SHORT).show()
                return false
            }
            if (intValue < neighbours.min) {
                Toast.makeText(this@EditCardsActivity, "Erro: O número [" + textView.text + "] é menor que [" + neighbours.min + "]", Toast.LENGTH_SHORT).show()
                return false
            }
            for (neighbour in neighbours.neighbours) {
                if (neighbour == textView) {
                    continue
                }
                if (textFromThisView == neighbour.text.toString()) {
                    Toast.makeText(this@EditCardsActivity, "Erro: número repetido: [" + textFromThisView + "]", Toast.LENGTH_SHORT).show()
                    return false
                    break
                }
            }
        } catch(nfe: NumberFormatException) {

        }
        return true
    }

}


