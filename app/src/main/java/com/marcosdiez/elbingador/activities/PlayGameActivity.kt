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
import com.marcosdiez.elbingador.databinding.PlayGameBinding


class PlayGameActivity : BingadorBasicActivity() {

    private lateinit var binding: PlayGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddNumber.setOnClickListener {
            clickHelper(true)
        }

        binding.buttonRemoveNumber.setOnClickListener {
            clickHelper(false)
        }

        binding.editTextNumber.setOnEditorActionListener(
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
        binding.editTextNumber.requestFocus()

        binding.checkboxShowOnlyWinningBingocards.setOnCheckedChangeListener { _, _ -> updateUi() }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun clickHelper(hit: Boolean) {
        if (binding.editTextNumber.text.isEmpty()) {
            return
        }

        val text = binding.editTextNumber.text.toString().replace(".","").replace(",","")

        binding.lastNumber.text = text

        try {
            val number = Integer.parseInt(text)
            if (hit) {
                bingoDeck.hit(number)
            } else {
                bingoDeck.unhit(number)
            }
            binding.editTextNumber.setText("")
            updateUi()
        }catch(e : NumberFormatException){
            Toast.makeText(this, String.format("[%s] não é um número válido", text), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi() {
        if(binding.checkboxShowOnlyWinningBingocards.isChecked){
            binding.results.text = bingoDeck.winningCardsToString()
        }   else {
            binding.results.text = bingoDeck.toString()
        }
    }
}
