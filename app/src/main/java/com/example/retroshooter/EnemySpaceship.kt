package com.example.retroshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class EnemySpaceship(context: Context) {
    private var enemySpaceship: Bitmap? = null
    var ey = 0
    var ex = 0
    var enemyVelocity = 0
    private var random: Random

    init {
        enemySpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.alien)
        random = Random()
        resetEnemySpaceship()
    }

    fun getEnemySpaceshipBitmap(): Bitmap? {
        return enemySpaceship
    }

    fun getEnemySpaceshipWidth(): Int {
        return enemySpaceship?.width ?: 0
    }

    fun getEnemySpaceshipHeight(): Int {
        return enemySpaceship?.height ?: 0
    }

    private fun resetEnemySpaceship() {
        ex = 200 + random.nextInt(400)
        ey = 0
        enemyVelocity = 14 + random.nextInt(10)
    }
}