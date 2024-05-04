package com.salastroya.bgandroid.services

import android.util.Log
import com.google.gson.GsonBuilder
import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.GardenNews
import com.salastroya.bgandroid.services.utilDate.LocalDateTimeDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.LocalDateTime


private interface NewsClient {
    @GET("api/news")
    suspend fun findAll(): List<GardenNews>
}

object NewsService {
    private val deserializerDate = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(
            GsonConverterFactory.create(deserializerDate)
        )
        .build()
    private val client: NewsClient = retrofit.create(NewsClient::class.java)

    private var cache: List<GardenNews> = emptyList()
    private var lastCacheUpdate: Long = 0L


    suspend fun findAll(): List<GardenNews> {
        if (shouldUpdateCache()) {
            try {
                cache = client.findAll().sortedBy { it.date }.reversed()
                lastCacheUpdate = System.currentTimeMillis()
            } catch (ex: Exception) {
                Log.e(null, ex.message ?: "Unexpected error calling API")
            }
        }

        return cache
    }

    private fun shouldUpdateCache(): Boolean {
        return System.currentTimeMillis() - lastCacheUpdate > 120_000L
    }

    suspend fun findById(id: Int): GardenNews? {
        try {
            return findAll()
                .firstOrNull {
                    it.id == id
                }
        } catch (ex: Exception) {
            Log.e(null, ex.message ?: "Unexpected error calling API")
        }
        return null
    }
}