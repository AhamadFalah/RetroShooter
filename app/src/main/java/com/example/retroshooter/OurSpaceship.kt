package com.example.retroshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class OurSpaceship(context: Context) {
    private var ourSpaceship: Bitmap? = null
    var oy = 0
    var ox = 0
    var isAlive = true
    private var random: Random

    init {
        ourSpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.rocket)
        random = Random()
        resetOurSpaceship()
    }

    fun getOurSpaceshipBitmap(): Bitmap? {
        return ourSpaceship
    }

    fun getOurSpaceshipWidth(): Int {
        return ourSpaceship?.width ?: 0
    }

    fun getOurSpaceshipHeight(): Int {
        return ourSpaceship?.height ?: 0
    }

    private fun resetOurSpaceship() {
        ox = 200 + random.nextInt(RetroShooter.screenWidth)
        oy = RetroShooter.screenHeight - getOurSpaceshipHeight()
    }
}