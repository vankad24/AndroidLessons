package com.molo4ko.cryptowatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.molo4ko.cryptowatch.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val historyAdapter = HistoryAdapter()

    private val intervalMillis = listOf(5000L, 30000L, 60000L, 300000L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initView()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.price.observe(this) { price ->
            binding.priceTextview.text = price
        }
        viewModel.history.observe(this) { history ->
            historyAdapter.submitList(history)
        }
    }

    private fun initView() {
        setupSpinners()
        setupRecyclerView()

        binding.refreshButton.setOnClickListener {
            fetchPrice()
        }

        lifecycleScope.launch {
            while (true) {
                val selectedInterval = intervalMillis[binding.intervalSpinner.selectedItemPosition]
                kotlinx.coroutines.delay(selectedInterval)
                fetchPrice()
            }
        }
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.crypto_currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.cryptoSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.fiat_currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fiatSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.intervals,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.intervalSpinner.adapter = adapter
        }

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchPrice()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.cryptoSpinner.onItemSelectedListener = spinnerListener
        binding.fiatSpinner.onItemSelectedListener = spinnerListener
    }

    private fun setupRecyclerView() {
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerview.adapter = historyAdapter
    }

    private fun fetchPrice() {
        val crypto = binding.cryptoSpinner.selectedItem.toString()
        val fiat = binding.fiatSpinner.selectedItem.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://min-api.cryptocompare.com/data/price?fsym=$crypto&tsyms=$fiat")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000 // 10 seconds
                connection.readTimeout = 10000 // 10 seconds

                val reader = InputStreamReader(connection.inputStream)
                val priceResponse = Gson().fromJson(reader, Map::class.java) as Map<String, Double>
                val price = priceResponse[fiat]

                withContext(Dispatchers.Main) {
                    if (price != null) {
                        val priceString = "1 $crypto = $price $fiat"
                        viewModel.updatePrice(priceString)
                    } else {
                        val errorMsg = "Error: Symbol '$fiat' not found in response."
                        viewModel.updatePrice(errorMsg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewModel.updatePrice("Error: ${e.message}")
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    inner class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

        private var historyList: List<String> = emptyList()

        fun submitList(list: List<String>) {
            historyList = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return HistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.bind(historyList[position])
        }

        override fun getItemCount() = historyList.size

        inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(text: String) {
                (itemView as TextView).text = text
            }
        }
    }
}