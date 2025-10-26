package com.molo4ko.addtwonumkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Обработчик нажатия на кнопку.
    fun onClick(v: View) {
        // Получаем ссылки на элементы разметки
        val etA = findViewById<EditText>(R.id.numA)
        val etB = findViewById<EditText>(R.id.numB)
        val tvSum = findViewById<TextView>(R.id.sum)

        // Получаем текст из полей ввода
        val strA = etA.text.toString()
        val strB = etB.text.toString()

        // 1. Используем toFloatOrNull() для поддержки вещественных чисел (Float)
        // 2. toFloatOrNull() также помогает реализовать проверку на пустые/некорректные значения
        val numA = strA.toFloatOrNull()
        val numB = strB.toFloatOrNull()

        if (numA != null && numB != null) {
            // Если оба числа успешно преобразованы (не null)
            val sum = numA + numB
            // Отображаем результат, преобразуя его обратно в строку
            tvSum.text = sum.toString()
        } else {
            // Если одно из полей пустое или содержит неверный формат
            val errorMessage = "Ошибка: Введите корректные числа в оба поля."
            tvSum.text = "---" // Сброс поля результата
            // Выводим всплывающее сообщение об ошибке
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}