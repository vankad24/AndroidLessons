package com.molo4ko.tempbinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.molo4ko.tempbinding.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WeatherAdapter(mutableListOf())

        binding.recyclerView.layoutManager =
            LinearLayoutManager(applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false)

        binding.recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            loadWeather("Irkutsk")
            loadWeather("Moscow")
            loadWeather("Sochi")
            loadWeather("London")
        }

    }

    fun loadWeather(city: String) {

        val API_KEY = getString(R.string.api_key)
        Log.d("weather", "started")
        val weatherURL =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric&lang=ru"

        try {

            val stream = URL(weatherURL).content as InputStream
            val data = Scanner(stream).nextLine()
            Log.d("weather", "data: $data")

            val gson = Gson()

            val weatherResponse =
                gson.fromJson(data, WeatherResponse::class.java)

            val weatherIcon =
                if (weatherResponse.weather[0].main == "Clouds")
                    R.drawable.cloud
                else
                    R.drawable.sun

            val windIcon =
                if (weatherResponse.wind.deg > 180)
                    R.drawable.wind_left
                else
                    R.drawable.wind_right

            val card = WeatherCard(
                weatherResponse.name,
                weatherResponse.main.temp,
                weatherResponse.main.humidity,
                weatherIcon,
                windIcon
            )

            runOnUiThread {
                adapter.add(card)
            }

        } catch (e: Exception) {
            Log.e("weather", "error", e)
        }
    }

    fun onClick(v: View) {

        val city = binding.cityEdit.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            loadWeather(city)
        }
    }
}