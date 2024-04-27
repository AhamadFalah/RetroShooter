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
    private var shootingFrequency = 400 // Initial shooting frequency (higher value means lower frequency)
    private var speed = 5 // Initial speed



    init {
        enemySpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.alien)
        random = Random()
        resetEnemySpaceship()
    }

    fun increaseShootingFrequency() {
        shootingFrequency -= 50 // Decrease the shooting frequency (increase the rate of shooting)
    }

    fun increaseSpeed() {
        speed += 2 // Increase the speed of the enemy spaceship
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