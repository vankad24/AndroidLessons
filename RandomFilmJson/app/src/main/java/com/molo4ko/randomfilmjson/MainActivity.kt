package com.molo4ko.randomfilmjson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.Random

// Класс модели данных (Movie.kt)
data class Movie(
    val id: Int,
    val title: String,
    val year: Int
)

class MainActivity : AppCompatActivity() {

    private val random = Random()

    private lateinit var movies: List<Movie>

    private lateinit var shownFilmIndices: IntArray
    private var index: Int = 0

    private val tvTitle: TextView by lazy { findViewById(R.id.text1) }
    private val tvYear: TextView by lazy { findViewById(R.id.tv_year) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movies = loadMoviesFromAsset("movies.json")

        if (movies.isNotEmpty()) {
            shownFilmIndices = IntArray(movies.size) { -1 }
        } else {
            Toast.makeText(this, "Не удалось загрузить фильмы.", Toast.LENGTH_LONG).show()
            shownFilmIndices = IntArray(0)
        }
    }

    /**
     * Читает JSON-файл из папки assets и парсит его в List<Movie>
     */
    private fun loadMoviesFromAsset(fileName: String): List<Movie> {
        val jsonString: String
        try {
            jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val listMovieType = object : TypeToken<List<Movie>>() {}.type
        return Gson().fromJson(jsonString, listMovieType)
    }

    fun nextClick(v: View) {
        if (movies.isEmpty()) {
            Toast.makeText(this, "Список фильмов пуст", Toast.LENGTH_SHORT).show()
            return
        }

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

        val movie = movies[newRandomIndex]

        tvTitle.text = movie.title
        tvYear.text = movie.year.toString()

        shownFilmIndices[index] = newRandomIndex
        index++

        println("Выбранный фильм: ${movie.title} (индекс: $newRandomIndex)")
    }

    fun resetClick(v: View) {
        if (movies.isNotEmpty()) {
            shownFilmIndices.fill(-1)
        }
        index = 0
        tvTitle.text = ""
        tvYear.text = ""
        Toast.makeText(this, "Сброс завершен", Toast.LENGTH_SHORT).show()
    }
}