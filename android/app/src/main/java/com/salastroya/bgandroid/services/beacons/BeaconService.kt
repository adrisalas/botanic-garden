package com.salastroya.bgandroid.services.beacons

import android.util.Log
import com.salastroya.bgandroid.BuildConfig
import org.altbeacon.beacon.Beacon
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private interface BeaconClient {
    @GET("api/beacons")
    suspend fun findAll(): List<com.salastroya.bgandroid.model.BeaconDto>
}

object BeaconService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val client: BeaconClient = retrofit.create(BeaconClient::class.java)

    private var cache: List<com.salastroya.bgandroid.model.BeaconDto> = emptyList()
    private var lastCacheUpdate: Long = 0L


    suspend fun findByBeacon(beacon: Beacon): com.salastroya.bgandroid.model.BeaconDto? {
        return findAll()
            .find { it.id == beacon.normalizeId() }
    }

    private suspend fun findAll(): List<com.salastroya.bgandroid.model.BeaconDto> {
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

    private fun Beacon.normalizeId(): String {
        val id1 = this.id1.toUuid()
        val id2 = this.id2.toInt()
        val id3 = this.id3.toInt()
        return "$id1-$id2-$id3".lowercase()
    }

    private fun shouldUpdateCache(): Boolean {
        return System.currentTimeMillis() - lastCacheUpdate > 120_000L
    }

}