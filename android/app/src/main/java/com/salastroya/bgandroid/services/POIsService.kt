package com.salastroya.bgandroid.services

import android.util.Log
import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.POI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private interface POIsClient {
    @GET("api/poi")
    suspend fun findAll(): List<POI>
}

object POIsService {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val client: POIsClient = retrofit.create(POIsClient::class.java)
    private var cache: List<POI> = emptyList()
    private var lastCacheUpdate: Long = 0L


    suspend fun findAll(): List<POI> {
        if (shouldUpdateCache()) {
            try {
                cache = client.findAll()
                lastCacheUpdate = System.currentTimeMillis()
            } catch (ex: Exception) {
                Log.e(null, ex.message ?: "Unexpected error calling API POI")
            }
        }

        return cache
    }

    private fun shouldUpdateCache(): Boolean {
        return System.currentTimeMillis() - lastCacheUpdate > 120_000L
    }
}