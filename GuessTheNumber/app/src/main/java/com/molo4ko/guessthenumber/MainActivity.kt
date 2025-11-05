package com.molo4ko.guessthenumber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onGuessClick(view: View) {
        val etBegin = findViewById<EditText>(R.id.begin)
        val etEnd = findViewById<EditText>(R.id.end)

        val beginText = etBegin.text.toString()
        val endText = etEnd.text.toString()

        val begin = beginText.toIntOrNull() ?: 0
        val end = endText.toIntOrNull() ?: 100

        // Проверяем корректность диапазона
        if (begin >= end) {
            Toast.makeText(this, "Конец диапазона должен быть строго больше начала!", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("begin", begin)
        intent.putExtra("end", end)
        startActivity(intent)
    }
}