package com.marcosdiez.elbingador.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.marcosdiez.elbingador.R
import kotlinx.android.synthetic.main.play_game.*

class PlayGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_game)


        button_add_number.setOnClickListener {
            val number = Integer.parseInt(editText_number.text.toString())

        }

        button_remove_number.setOnClickListener {
            val number = Integer.parseInt(editText_number.text.toString())

        }

    }
}
