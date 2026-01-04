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
import androidx.appcompat.app.AppCompatActivity
import com.marcosdiez.elbingador.BingoCard
import com.marcosdiez.elbingador.BingoDeck
import com.marcosdiez.elbingador.R
import com.marcosdiez.elbingador.databinding.BingoCardBinding
import java.util.HashMap
import kotlin.collections.ArrayList


data class BingoRestriction(val neighbours: Set<EditText>, val min: Int = 0, val max: Int = 99)


class EditCardsActivity : BingadorBasicActivity() {

    private lateinit var binding: BingoCardBinding
    private lateinit var numberSet: Set<EditText>
    private lateinit var numberMatrix: ArrayList<ArrayList<EditText>>
    private val numberRowRules = HashMap<EditText, BingoRestriction>()
    private val previousCell = HashMap<EditText, EditText>()


    private lateinit var bingoCardBeingDisplayed: BingoCard
    private var bingoDeckDisplayedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BingoCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCollections()

        fixKeyboardNextButton(numberSet)
        binding.number00.requestFocus()

        binding.buttonClearCells.setOnClickListener {
            for (aNumber in numberSet) {
                aNumber.setText("")
            }
            binding.number00.requestFocus()
        }

        binding.buttonNew.setOnClickListener {
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

        binding.buttonBack.setOnClickListener {
            if (bingoDeckDisplayedIndex > 0) {
                changeCardHelper(-1)
            }
        }

        binding.buttonNext.setOnClickListener {
            if (bingoDeckDisplayedIndex < (bingoDeck.bingoDeck.size - 1)) {
                changeCardHelper(1)
            }
        }

        binding.buttonPlay.setOnClickListener {
            val myIntent = Intent(this, PlayGameActivity::class.java)
            startActivity(myIntent)
        }

        binding.buttonFillRandom.setOnClickListener {
            for (n in numberSet) {
                if (n.text.toString() == "") {
                    n.setText(((Math.round(Math.random() * 10000).toInt() % 98) + 1).toString())
                }
            }
        }


        binding.buttonEraseAllCards.setOnClickListener {
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
        binding.buttonBack.isEnabled = bingoDeckDisplayedIndex != 0
        binding.buttonNext.isEnabled = bingoDeckDisplayedIndex != (bingoDeck.bingoDeck.size - 1)
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
        if(::bingoCardBeingDisplayed.isInitialized) {
            bingoCardBeingDisplayed = uiToBingoCard()
        }
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
        binding.cardTitle.setText(bingoCard.name)
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
        binding.textViewStatus.text = String.format("Cartela %d de %d", bingoDeckDisplayedIndex + 1, bingoDeck.bingoDeck.size)
    }

    private fun uiToBingoCard(): BingoCard {
        bingoCardBeingDisplayed.name = binding.cardTitle.text.toString()

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
                binding.number00, binding.number01, binding.number02, binding.number03, binding.number04,
                binding.number10, binding.number11, binding.number12, binding.number13, binding.number14,
                binding.number20, binding.number21, binding.number22, binding.number23, binding.number24,
                binding.number30, binding.number31, binding.number32, binding.number33, binding.number34,
                binding.number40, binding.number41, binding.number42, binding.number43, binding.number44)

        numberMatrix = ArrayList<ArrayList<EditText>>(
                listOf(
                        ArrayList<EditText>(listOf(binding.number00, binding.number01, binding.number02, binding.number03, binding.number04)),
                        ArrayList<EditText>(listOf(binding.number10, binding.number11, binding.number12, binding.number13, binding.number14)),
                        ArrayList<EditText>(listOf(binding.number20, binding.number21, binding.number22, binding.number23, binding.number24)),
                        ArrayList<EditText>(listOf(binding.number30, binding.number31, binding.number32, binding.number33, binding.number34)),
                        ArrayList<EditText>(listOf(binding.number40, binding.number41, binding.number42, binding.number43, binding.number44))
                )
        )

        val b = BingoRestriction(setOf(binding.number00, binding.number10, binding.number20, binding.number30, binding.number40), 1, 15)
        numberRowRules[binding.number00] = b
        numberRowRules[binding.number10] = b
        numberRowRules[binding.number20] = b
        numberRowRules[binding.number30] = b
        numberRowRules[binding.number40] = b

        val i = BingoRestriction(setOf(binding.number01, binding.number11, binding.number21, binding.number31, binding.number41), 16, 30)
        numberRowRules[binding.number01] = i
        numberRowRules[binding.number11] = i
        numberRowRules[binding.number21] = i
        numberRowRules[binding.number31] = i
        numberRowRules[binding.number41] = i

        val n = BingoRestriction(setOf(binding.number02, binding.number12, binding.number22, binding.number32, binding.number42), 31, 45)
        numberRowRules[binding.number02] = n
        numberRowRules[binding.number12] = n
        numberRowRules[binding.number22] = n
        numberRowRules[binding.number32] = n
        numberRowRules[binding.number42] = n

        val g = BingoRestriction(setOf(binding.number03, binding.number13, binding.number23, binding.number33, binding.number43), 46, 60)
        numberRowRules[binding.number03] = g
        numberRowRules[binding.number13] = g
        numberRowRules[binding.number23] = g
        numberRowRules[binding.number33] = g
        numberRowRules[binding.number43] = g

        val o = BingoRestriction(setOf(binding.number04, binding.number14, binding.number24, binding.number34, binding.number44), 61, 75)
        numberRowRules[binding.number04] = o
        numberRowRules[binding.number14] = o
        numberRowRules[binding.number24] = o
        numberRowRules[binding.number34] = o
        numberRowRules[binding.number44] = o


        previousCell[binding.number00] = binding.number00
        previousCell[binding.number10] = binding.number00
        previousCell[binding.number20] = binding.number10
        previousCell[binding.number30] = binding.number20
        previousCell[binding.number40] = binding.number30

        previousCell[binding.number01] = binding.number40
        previousCell[binding.number11] = binding.number01
        previousCell[binding.number21] = binding.number11
        previousCell[binding.number31] = binding.number21
        previousCell[binding.number41] = binding.number31

        previousCell[binding.number02] = binding.number41
        previousCell[binding.number12] = binding.number02
        previousCell[binding.number22] = binding.number12
        previousCell[binding.number32] = binding.number22
        previousCell[binding.number42] = binding.number32

        previousCell[binding.number03] = binding.number42
        previousCell[binding.number13] = binding.number03
        previousCell[binding.number23] = binding.number13
        previousCell[binding.number33] = binding.number23
        previousCell[binding.number43] = binding.number33

        previousCell[binding.number04] = binding.number43
        previousCell[binding.number14] = binding.number04
        previousCell[binding.number24] = binding.number14
        previousCell[binding.number34] = binding.number24
        previousCell[binding.number44] = binding.number43
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
//                    if (!binding.checkboxModoBebedouro.isChecked) {
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
                                        if(binding.checkboxModoBebedouro.isChecked){
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
            if (textView == binding.number22 && intValue == 0) {   // zero is accepted in the middle row
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
                }
            }
        } catch(nfe: NumberFormatException) {

        }
        return true
    }

}


