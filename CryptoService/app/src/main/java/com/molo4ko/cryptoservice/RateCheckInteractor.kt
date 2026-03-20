package com.molo4ko.cryptoservice

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal

class RateCheckInteractor {
    // Запрос курса Ethereum к USD
    val RATE_URL = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD"
    val networkClient = NetworkClient()

    suspend fun requestRate(): BigDecimal? {
        return withContext(Dispatchers.IO) {
            val result = networkClient.request(RATE_URL)
            if (!result.isNullOrEmpty()) {
                parseRate(result)
            } else {
                null
            }
        }
    }

    private fun parseRate(jsonString: String): BigDecimal? {
        return try {
            val json = JSONObject(jsonString)
            val usdRate = json.getDouble("USD")
            BigDecimal(usdRate)
        } catch (e: Exception) {
            Log.e("RateCheckInteractor", "Error parsing rate", e)
            null
        }
    }
}