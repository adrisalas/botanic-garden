package com.salastroya.bgandroid.services

import android.util.Log
import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.GamificationPoints
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.auth.JWTService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

private interface GamificationClient {
    @GET("api/gamification/find-plant")
    suspend fun findActivePlant(): Plant

    @GET("api/gamification/my-points")
    suspend fun findMyPoints(@Header("authorization") token: String): GamificationPoints
}

object GamificationService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val client: GamificationClient = retrofit.create(GamificationClient::class.java)


    suspend fun findActivePlant(): Plant? {
        var plant: Plant;
        try {
            plant = client.findActivePlant()
            return plant
        } catch (ex: Exception) {
            return null
            Log.e(null, ex.message ?: "Unexpected error calling API")
        }
    }

    suspend fun findMyPoints(): GamificationPoints? {
        var points: GamificationPoints;
        try {
            points = client.findMyPoints("Bearer ${JWTService.jwtStore}")
            return points
        } catch (ex: Exception) {
            return null
            Log.e(null, ex.message ?: "Unexpected error calling API")
        }
    }

}