package com.salastroya.bgandroid

import android.app.Application
import com.salastroya.bgandroid.services.auth.JWTService

class BotanicGardenApplication : Application() {

    companion object {
        const val TAG = "BotanicGardenApplication"
        lateinit var instance: Application
        const val PREFERENCES = "bg-preferences"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        JWTService.sharedPreferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val previousJWT = JWTService.getJwtFromPreferences(this)
        JWTService.storeJwt(this, previousJWT)
    }
}