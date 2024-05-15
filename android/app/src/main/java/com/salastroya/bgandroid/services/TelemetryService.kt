package com.salastroya.bgandroid.services

import android.util.Log
import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.TelemetryData
import com.salastroya.bgandroid.services.auth.JWTService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

private interface TelemetryClient {
    @POST("api/telemetry")
    suspend fun sendTelemetry(
        @Header("authorization") token: String,
        @Body data: TelemetryData
    )
}

object TelemetryService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val client: TelemetryClient = retrofit.create(TelemetryClient::class.java)

    suspend fun sendTelemetry(beaconSeen: String) {
        try {
            client.sendTelemetry(
                "Bearer ${JWTService.jwtStore}",
                TelemetryData(beaconSeen)
            )
        } catch (ex: Exception) {
            Log.e(null, "Error sending telemetry data", ex)
        }
    }
}