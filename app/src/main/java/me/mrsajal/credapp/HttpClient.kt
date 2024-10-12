package me.mrsajal.credapp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import android.util.Log

val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}

suspend fun fetchItemsFromApi(): List<Item> {
    val apiUrl = "https://api.mocklets.com/p6764/test_mint"
    return try {
        val response: ApiResponse = client.get(apiUrl).body()
        response.items
    } catch (e: Exception) {
        Log.e("fetchItemsFromApi", "Error fetching data", e)
        emptyList() // Return empty list in case of an error
    }
}
