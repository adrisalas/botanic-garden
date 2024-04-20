package com.salastroya.bgandroid.services

import android.util.Log
import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.Plant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PlantClient {
    @GET("api/plants")
    suspend fun findAll(): List<Plant>
}

object PlantService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val client: PlantClient = retrofit.create(PlantClient::class.java)

    private var cache: List<Plant> = emptyList()
    private var lastCacheUpdate: Long = 0L


    suspend fun findAll(): List<Plant> {
        if (shouldUpdateCache()) {
            try {
                cache = client.findAll()
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

    suspend fun findById(id: Int): Plant? {
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