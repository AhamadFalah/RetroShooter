package com.example.retroshooter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.util.*

class RetroShooter(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var background: Bitmap? = null
    private var scaledBackground: Bitmap? = null
    private var lifeImage: Bitmap? = null
    private var gameHandler: Handler? = null
    private val updateMillis = 36L

    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }

    private var life = 3
    var points = 0
    private val textSize = 80
    private var paused = false
    private var ourSpaceship: OurSpaceship? = null
    private var enemySpaceship: EnemySpaceship? = null
    private var random: Random? = null
    private var enemyShots: ArrayList<Shot> = ArrayList()
    private var ourShots: ArrayList<Shot> = ArrayList()
    private var enemyExplosion = false
    private var explosion: Explosion? = null
    private var explosions: ArrayList<Explosion> = ArrayList()
    private var enemyShotAction = false
    private val scorePaint: Paint = Paint().apply {
        color = Color.RED
        textSize = 75F
        textAlign = Paint.Align.LEFT
        val customTypeface = ResourcesCompat.getFont(context, R.font.retropix)
        typeface = customTypeface
    }

    private var increaseShootingFrequencyAt = 10 // Initial points threshold

    private val runnable = object : Runnable {
        override fun run() {
            invalidate()
        }
    }

    init {
        random = Random()
        val display = (context as Activity).windowManager.defaultDisplay
        val size = android.graphics.Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
        ourSpaceship = OurSpaceship(context)
        enemySpaceship = EnemySpaceship(context)
        background = BitmapFactory.decodeResource(context.resources, R.drawable.background)
        lifeImage = BitmapFactory.decodeResource(context.resources, R.drawable.heart)
        gameHandler = Handler()

        // Scale the background image to fit the screen
        val backgroundWidth = background?.width ?: 0
        val backgroundHeight = background?.height ?: 0
        val scaleX = screenWidth.toFloat() / backgroundWidth
        val scaleY = screenHeight.toFloat() / backgroundHeight
        val matrix = Matrix().apply {
            postScale(scaleX, scaleY)
        }
        scaledBackground = Bitmap.createBitmap(background!!, 0, 0, backgroundWidth, backgroundHeight, matrix, true)
    }

    override fun onDraw(canvas: Canvas) {
        scaledBackground?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        canvas.drawText("Pt: $points", 0f, textSize.toFloat(), scorePaint)
        for (i in life downTo 1) {
            lifeImage?.let {
                canvas.drawBitmap(it, (screenWidth - it.width * i).toFloat(), 0f, null)
            }
        }

        if (life == 0) {
            paused = true
            gameHandler = null
            val intent = Intent(context, GameOver::class.java)
            intent.putExtra("points", points)
            context.startActivity(intent)
        }

        enemySpaceship?.let { enemyShip ->
            enemyShip.ex += enemyShip.enemyVelocity
            if (enemyShip.ex + enemyShip.getEnemySpaceshipWidth() >= screenWidth) {
                enemyShip.enemyVelocity *= -1
            }

            if (enemyShip.ex <= 0) {
                enemyShip.enemyVelocity *= -1
            }

            if (!enemyShotAction && (enemyShip.ex >= 200 + random!!.nextInt(400 - points / increaseShootingFrequencyAt * 100))) {
                val enemyShot = Shot(context, enemyShip.ex + enemyShip.getEnemySpaceshipWidth() / 2, enemyShip.ey)
                enemyShots.add(enemyShot)
                enemyShotAction = true
            }
        }

        if (points % increaseShootingFrequencyAt == 0) {
            increaseShootingFrequencyAt += 10 // Adjust this value as needed
            enemySpaceship?.increaseShootingFrequency()
            enemySpaceship?.increaseSpeed()
        }

        if (!paused) {
            gameHandler?.postDelayed(runnable, updateMillis)
        }
        if (!enemyExplosion) {
            enemySpaceship?.getEnemySpaceshipBitmap()?.let { enemyBitmap ->
                canvas.drawBitmap(enemyBitmap, enemySpaceship!!.ex.toFloat(), enemySpaceship!!.ey.toFloat(), null)
            }
        }

        if (ourSpaceship?.isAlive == true) {
            if (ourSpaceship!!.ox > screenWidth - ourSpaceship!!.getOurSpaceshipWidth()) {
                ourSpaceship!!.ox = screenWidth - ourSpaceship!!.getOurSpaceshipWidth()
            } else if (ourSpaceship!!.ox < 0) {
                ourSpaceship!!.ox = 0
            }
            ourSpaceship?.getOurSpaceshipBitmap()?.let { ourBitmap ->
                canvas.drawBitmap(ourBitmap, ourSpaceship!!.ox.toFloat(), ourSpaceship!!.oy.toFloat(), null)
            }
        }

        var i = 0
        while (i < enemyShots.size) {
            enemyShots[i].shy += 15
            enemyShots[i].getShot()?.let {
                canvas.drawBitmap(it, enemyShots[i].shx.toFloat(), enemyShots[i].shy.toFloat(), null)
            }
            if ((enemyShots[i].shx >= ourSpaceship!!.ox) && (enemyShots[i].shx <= ourSpaceship!!.ox + ourSpaceship!!.getOurSpaceshipWidth()) && (enemyShots[i].shy >= ourSpaceship!!.oy) && (enemyShots[i].shy <= screenHeight)) {
                life--
                enemyShots.removeAt(i)
                explosion = Explosion(context, ourSpaceship!!.ox, ourSpaceship!!.oy)
                explosions.add(explosion!!)
            } else if (enemyShots[i].shy >= screenHeight) {
                enemyShots.removeAt(i)
            }
            if (enemyShots.isEmpty()) {
                enemyShotAction = false
            }
            i++
        }

        i = 0
        while (i < ourShots.size) {
            ourShots[i].shy -= 15
            ourShots[i].getShot()?.let {
                canvas.drawBitmap(it, ourShots[i].shx.toFloat(), ourShots[i].shy.toFloat(), null)
            }
            if ((ourShots[i].shx >= enemySpaceship!!.ex) && (ourShots[i].shx <= enemySpaceship!!.ex + enemySpaceship!!.getEnemySpaceshipWidth()) && (ourShots[i].shy <= enemySpaceship!!.ey + enemySpaceship!!.getEnemySpaceshipHeight()) && (ourShots[i].shy >= enemySpaceship!!.ey)) {
                points++
                if (points % increaseShootingFrequencyAt == 0) {
                    increaseShootingFrequencyAt += 10 // Adjust this value as needed
                }
                ourShots.removeAt(i)
                explosion = Explosion(context, enemySpaceship!!.ex, enemySpaceship!!.ey)
                explosions.add(explosion!!)
            } else if (ourShots[i].shy <= 0) {
                ourShots.removeAt(i)
            }
            i++
        }

        i = 0
        while (i < explosions.size) {
            explosions[i].getExplosion(explosions[i].explosionFrame)?.let {
                canvas.drawBitmap(it, explosions[i].ex.toFloat(), explosions[i].ey.toFloat(), null)
            }
            explosions[i].explosionFrame++
            if (explosions[i].explosionFrame > 8) {
                explosions.removeAt(i)
            }
            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (ourShots.size < 3) {
                    val ourShot = Shot(context, ourSpaceship!!.ox + ourSpaceship!!.getOurSpaceshipWidth() / 2, ourSpaceship!!.oy)
                    ourShots.add(ourShot)
                }
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                ourSpaceship?.ox = touchX
            }
        }
        return true
    }
}