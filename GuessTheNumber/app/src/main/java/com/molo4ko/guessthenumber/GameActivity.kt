package com.molo4ko.guessthenumber


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    // Начало диапазона (включительно)
    var begin: Int = 0
    // Конец диапазона (включительно)
    var end: Int = 100
    // Текущее число, которое мы спрашиваем (точка разделения или окончательная догадка)
    private var currentGuess: Int = -1

    private lateinit var tvQuestion: TextView
    private lateinit var btnYes: Button
    private lateinit var btnNo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        begin = intent.getIntExtra("begin", 0)
        end = intent.getIntExtra("end", 100)

        tvQuestion = findViewById(R.id.question)
        btnYes = findViewById(R.id.yes)
        btnNo = findViewById(R.id.no)

        Log.d("mytag", "Начало = $begin, Конец = $end")

        // Запуск первого вопроса
        askNextQuestion()
    }

    private fun askNextQuestion() {
        Log.d("mytag", "Новый диапазон: [$begin, $end]")

        if (begin == end) {
            tvQuestion.text = "УРА! Ваше число: $begin! Попробуйте загадать снова."
            disableButtons()
            return
        }

        // Проверка, что разница между begin и end == 1 (осталось 2 соседних числа)
        // Если осталось два соседних числа, спрашиваем про большее из них (end)
        if (end - begin == 1) {
            currentGuess = end
            tvQuestion.text = "Ваше число: $currentGuess?"
            // Обновляем текст на кнопках для ясности
            btnYes.text = "Да (это $currentGuess)"
            btnNo.text = "Нет (значит это $begin)"
            return
        }

        // Стандартный шаг бинарного поиска: разбиваем диапазон пополам
        currentGuess = begin + (end - begin) / 2

        tvQuestion.text = "Ваше число больше $currentGuess?"

        btnYes.text = "Да (больше $currentGuess)"
        btnNo.text = "Нет (меньше или равно $currentGuess)"
    }


    fun onYesNoClick(view: View) {
        // Если игра завершена, не обрабатываем клики
        if (begin == end) return

        when (view.id) {
            R.id.yes -> {
                Log.d("mytag", "Клик: Да (Yes). Текущая догадка: $currentGuess")

                if (end - begin == 1 && currentGuess == end) {
                    // Обработка финального вопроса
                    begin = currentGuess
                } else {
                    begin = currentGuess + 1
                }
            }
            R.id.no -> {
                Log.d("mytag", "Клик: Нет (No). Текущая догадка: $currentGuess")

                if (end - begin == 1 && currentGuess == end) {
                    // Обработка финального вопроса
                    end = begin
                } else {
                    end = currentGuess
                }
            }
        }

        // Задаем следующий вопрос с обновленными границами
        askNextQuestion()
    }

    /**
     * Вспомогательная функция для отключения кнопок после завершения игры.
     */
    private fun disableButtons() {
        btnYes.isEnabled = false
        btnNo.isEnabled = false
    }
}