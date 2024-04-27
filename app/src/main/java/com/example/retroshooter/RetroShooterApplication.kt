package com.example.retroshooter

import android.app.Application

class RetroShooterApplication : Application() {
    companion object {
        lateinit var scoreManager: ScoreManager
    }

    override fun onCreate() {
        super.onCreate()
        scoreManager = ScoreManager(this)
    }
}