package com.molo4ko.randomfilmkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.Random

class MainActivity : AppCompatActivity() {

    private val random = Random()

    private lateinit var movies: Array<String>
    private lateinit var shownFilmIndices: IntArray
    private var index: Int = 0

    private val tvTitle: TextView by lazy { findViewById(R.id.text1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movies = resources.getStringArray(R.array.movies)
        shownFilmIndices = IntArray(movies.size) { -1 }
    }

    fun nextClick(v: View) {
        if (index >= shownFilmIndices.size) {
            Toast.makeText(this, "Фильмы закончились", Toast.LENGTH_SHORT).show()
            return
        }

        var newRandomIndex: Int
        var isIndexShown: Boolean

        do {
            newRandomIndex = random.nextInt(movies.size)
            isIndexShown = newRandomIndex in shownFilmIndices
        } while (isIndexShown)

        val movieTitle = movies[newRandomIndex]

        tvTitle.text = movieTitle

        shownFilmIndices[index] = newRandomIndex
        index++

        println("Выбранный индекс: $newRandomIndex")
        println("Длина массива фильмов: ${movies.size} len")
    }

    fun resetClick(v: View) {
        shownFilmIndices.fill(-1)
        index = 0
        tvTitle.text = ""
        Toast.makeText(this, "Сброс завершен", Toast.LENGTH_SHORT).show()
    }
}