package com.olafros.exercise2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class RandomNumberActivity : AppCompatActivity() {
    var upperLimit = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_number)
        upperLimit = intent.getIntExtra("upperLimit", upperLimit)
    }

    /**
     * Generates 10 random numbers between 0 and the upper limit for the receiver to use
     */
    fun generateRandomNumber(v: View?) {
        val intent = Intent()
        for (i in 0..10) {
            intent.putExtra("randomNumber$i", (0..upperLimit).random())
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}