package com.example.retroshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class PlayerRocket(context: Context) {
    private var playerRocket: Bitmap? = null
    var oy = 0
    var ox = 0
    var isAlive = true
    private var random: Random

    init {
        playerRocket = BitmapFactory.decodeResource(context.resources, R.drawable.rocket)
        random = Random()
        resetPlayerRocket()
    }

    fun getPlayerRocketBitmap(): Bitmap? {
        return playerRocket
    }

    fun getPlayerRocketWidth(): Int {
        return playerRocket?.width ?: 0
    }

    fun getPlayerRocketHeight(): Int {
        return playerRocket?.height ?: 0
    }

    private fun resetPlayerRocket() {
        ox = 200 + random.nextInt(RetroShooter.screenWidth)
        oy = RetroShooter.screenHeight - getPlayerRocketHeight()
    }
}