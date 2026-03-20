package com.molo4ko.cryptoservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class RateCheckService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var rateCheckAttempt = 0
    private lateinit var startRate: BigDecimal
    private lateinit var targetRate: BigDecimal
    private val rateCheckInteractor = RateCheckInteractor()
    private var notificationManager: NotificationManager? = null
    private val CHANNEL_ID = "rate_check_channel"
    private val NOTIFICATION_ID = 1

    private val rateCheckRunnable = object : Runnable {
        override fun run() {
            requestAndCheckRate()
        }
    }

    private fun requestAndCheckRate() {
        rateCheckAttempt++
        CoroutineScope(Dispatchers.Main).launch {
            val currentRate = rateCheckInteractor.requestRate()
            if (currentRate != null) {
                Log.d(TAG, "Current rate: $currentRate")
                val difference = currentRate.subtract(startRate)
                val absDifference = difference.abs()
                if (absDifference >= targetRate) {
                    val direction = if (difference > BigDecimal.ZERO) "↑" else "↓"
                    val directionFlag = if (difference > BigDecimal.ZERO) "up" else "down"
                    val changeAmountRounded = absDifference.setScale(4, BigDecimal.ROUND_HALF_UP)
                    val message = "ETH/USD изменился на $changeAmountRounded USD $direction"

                    showToast("Условие выполнено: $message")
                    sendNotification(message, directionFlag)

                    // Уведомляем активность об остановке сервиса
                    sendStopBroadcast()
                    stopSelf()
                } else {
                    if (rateCheckAttempt < RATE_CHECK_ATTEMPTS_MAX) {
                        handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
                    } else {
                        Log.d(TAG, "Достигнут максимум попыток, сервис останавливается")
                        sendStopBroadcast()
                        stopSelf()
                    }
                }
            } else {
                if (rateCheckAttempt < RATE_CHECK_ATTEMPTS_MAX) {
                    handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
                } else {
                    sendStopBroadcast()
                    stopSelf()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRate = BigDecimal(intent?.getStringExtra(ARG_START_RATE) ?: return START_NOT_STICKY)
        targetRate = BigDecimal(intent?.getStringExtra(ARG_TARGET_RATE) ?: return START_NOT_STICKY)

        Log.d(TAG, "onStartCommand startRate = $startRate targetRate = $targetRate")

        rateCheckAttempt = 0
        handler.post(rateCheckRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(rateCheckRunnable)
        // Уведомляем о завершении (например, если остановили вручную)
        sendStopBroadcast()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rate Check Service",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for rate change notifications"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(message: String, direction: String) {
        val iconRes = when (direction) {
            "up" -> android.R.drawable.arrow_up_float
            else -> android.R.drawable.arrow_down_float
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Изменение курса ETH/USD")
            .setContentText(message)
            .setSmallIcon(iconRes)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun sendStopBroadcast() {
        val intent = Intent(ACTION_SERVICE_STOPPED)
        sendBroadcast(intent)
    }

    companion object {
        const val TAG = "RateCheckService"
        const val RATE_CHECK_INTERVAL = 5000L
        const val RATE_CHECK_ATTEMPTS_MAX = 100
        const val ARG_START_RATE = "ARG_START_RATE"
        const val ARG_TARGET_RATE = "ARG_TARGET_RATE"
        const val ACTION_SERVICE_STOPPED = "com.molo4ko.cryptoservice.SERVICE_STOPPED"

        fun startService(context: Context, startRate: String, targetRate: String) {
            val intent = Intent(context, RateCheckService::class.java).apply {
                putExtra(ARG_START_RATE, startRate)
                putExtra(ARG_TARGET_RATE, targetRate)
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, RateCheckService::class.java))
        }
    }
}