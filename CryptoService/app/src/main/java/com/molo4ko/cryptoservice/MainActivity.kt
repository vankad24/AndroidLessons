package com.molo4ko.cryptoservice

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var etStartRate: EditText
    private lateinit var etTargetChange: EditText
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnRefresh: Button
    private lateinit var tvCurrentRate: TextView

    private val rateCheckInteractor = RateCheckInteractor()

    private val serviceStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == RateCheckService.ACTION_SERVICE_STOPPED) {
                updateButtonsState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCurrentRate = findViewById(R.id.tvCurrentRate)
        etStartRate = findViewById(R.id.etStartRate)
        etTargetChange = findViewById(R.id.etTargetChange)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnRefresh = findViewById(R.id.btnRefresh)

        // Запрос разрешения на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        refreshCurrentRate()
        updateButtonsState()

        btnRefresh.setOnClickListener {
            refreshCurrentRate()
        }

        btnStart.setOnClickListener {
            val startRate = etStartRate.text.toString()
            val targetChange = etTargetChange.text.toString()
            if (startRate.isNotEmpty() && targetChange.isNotEmpty()) {
                RateCheckService.startService(this, startRate, targetChange)
                Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show()
                updateButtonsState()
            } else {
                Toast.makeText(this, R.string.enter_both, Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            RateCheckService.stopService(this)
            Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show()
            updateButtonsState()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(serviceStopReceiver, IntentFilter(RateCheckService.ACTION_SERVICE_STOPPED))
        updateButtonsState()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(serviceStopReceiver)
    }

    private fun updateButtonsState() {
        val isServiceRunning = isServiceRunning(RateCheckService::class.java)
        btnStart.isEnabled = !isServiceRunning
        btnStop.isEnabled = isServiceRunning
        etStartRate.isEnabled = !isServiceRunning
        etTargetChange.isEnabled = !isServiceRunning
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun refreshCurrentRate() {
        lifecycleScope.launch {
            val rate = withContext(Dispatchers.IO) {
                rateCheckInteractor.requestRate()
            }
            if (rate != null) {
                tvCurrentRate.text = getString(R.string.current_rate, rate.toString())
            } else {
                tvCurrentRate.text = getString(R.string.current_rate, "ошибка загрузки")
            }
        }
    }
}