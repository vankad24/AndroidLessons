package com.molo4ko.cryptoservice

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class NetworkClient {
    private val client = OkHttpClient()

    fun request(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}