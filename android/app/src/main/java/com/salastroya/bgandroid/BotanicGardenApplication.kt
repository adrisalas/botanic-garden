package com.salastroya.bgandroid

import android.app.Application

class BotanicGardenApplication : Application() {

    companion object {
        const val TAG = "BotanicGardenApplication"
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}