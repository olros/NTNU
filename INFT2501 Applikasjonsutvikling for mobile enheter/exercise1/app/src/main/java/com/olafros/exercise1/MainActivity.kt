package com.olafros.exercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private val firstName = "Olaf"
    private val lastName = "Rosendahl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(meny: Menu): Boolean{
        super.onCreateOptionsMenu(meny)
        meny.add(firstName)
        meny.add(lastName)
        Log.d("INFT2501","meny laget")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when (item.title.toString()) {
            firstName -> Log.w("Menyvalg", item.title.toString())
            lastName -> Log.e("Menyvalg", item.title.toString())
        }
        return true
    }
}