package com.olafros.exercise2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ActivityTaskA : AppCompatActivity() {
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    if (result.data != null) {
                        val generatedNumber = result.data!!.getIntExtra("randomNumber1", 5)
                        Toast.makeText(this, "Generert tall: $generatedNumber", Toast.LENGTH_LONG)
                            .show()
                        val textView = findViewById<View>(R.id.task_a_textview_result) as TextView
                        textView.text = "Generert tall: $generatedNumber"
                    }
                }
                else -> {
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_a)
    }

    fun onClickRandomNumberActivity(v: View?) {
        startForResult.launch(Intent(this, RandomNumberActivity::class.java))
    }
}