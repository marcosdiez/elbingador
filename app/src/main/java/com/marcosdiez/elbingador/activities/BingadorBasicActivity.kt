package com.marcosdiez.elbingador.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.marcosdiez.elbingador.BingoDeck
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

abstract class BingadorBasicActivity : AppCompatActivity() {

    protected lateinit var bingoDeck: BingoDeck
    private val FILENAME = "bingoDeck.raw"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun saveDataToDisk() {
        val fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(bingoDeck)
        objectOutputStream.flush()
        fileOutputStream.close()
    }

    override fun onPause() {
        saveDataToDisk()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        bingoDeck = loadDataFromDisk()
        bingoDeck.recalculateNumberMap()

    }

    private fun loadDataFromDisk() : BingoDeck {
        try {
            val fileInputStream = openFileInput(FILENAME)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val serializedBingoDeck = objectInputStream.readObject() as BingoDeck
            objectInputStream.close()
            fileInputStream.close()
            return serializedBingoDeck
        } catch(e: FileNotFoundException) {
            return BingoDeck()
        }
    }
}
