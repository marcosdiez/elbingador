package com.marcosdiez.elbingador.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.marcosdiez.elbingador.BingoDeck
import com.marcosdiez.elbingador.R
import kotlinx.android.synthetic.main.bingo_card.*
import kotlinx.android.synthetic.main.play_game.*


class PlayGameActivity : BingadorBasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_game)

        button_add_number.setOnClickListener {
            clickHelper(true)
        }

        button_remove_number.setOnClickListener {
            clickHelper(false)
        }

        editText_number.setOnEditorActionListener(
            object : TextView.OnEditorActionListener {
                override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (textView != null) {
                            if (textView.text.isNotEmpty()) {
                                // there is some data here
                                clickHelper(true)
                                return true // so we move to the next square
                            } else {
                                return false // so we stay in this square
                            }
                        }
                    }
                    return false
                }
            }
        )
        editText_number.requestFocus()

        checkbox_show_only_winning_bingocards.setOnCheckedChangeListener { _, _ -> updateUi() }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    fun clickHelper(hit: Boolean) {
        if (editText_number.text.isEmpty()) {
            return
        }

        val text = editText_number.text.toString().replace(".","").replace(",","")

        last_number.text = text

        try {
            val number = Integer.parseInt(text)
            if (hit) {
                bingoDeck.hit(number)
            } else {
                bingoDeck.unhit(number)
            }
            editText_number.setText("")
            updateUi()
        }catch(e : NumberFormatException){
            Toast.makeText(this, String.format("[%s] não é um número válido", text), Toast.LENGTH_SHORT).show()
        }
    }

    fun updateUi() {
        if(checkbox_show_only_winning_bingocards.isChecked){
            results.text = bingoDeck.winningCardsToString()
        }   else {
            results.text = bingoDeck.toString()
        }
    }
}
