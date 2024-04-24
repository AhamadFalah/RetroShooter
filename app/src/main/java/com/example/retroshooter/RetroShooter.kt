package com.example.retroshooter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class RetroShooter(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    var background: Bitmap? = null
    var lifeImage: Bitmap? = null
    var gameHandler: Handler? = null // Renamed from 'handler'
    val UPDATE_MILLIS = 36L

    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }

    var life = 3
    var points = 0
    val TEXT_SIZE = 80
    var paused = false
    var ourSpaceship: OurSpaceship? = null
    var enemySpaceship: EnemySpaceship? = null
    var random: Random? = null
    var enemyShots: ArrayList<Shot> = ArrayList()
    var ourShots: ArrayList<Shot> = ArrayList()
    var enemyExplosion = false
    var explosion: Explosion? = null
    var explosions: ArrayList<Explosion> = ArrayList()
    var enemyShotAction = false
    var scorePaint: Paint = Paint().apply {
        color = Color.RED
        textSize = TEXT_SIZE.toFloat()
        textAlign = Paint.Align.LEFT
    }

    val runnable = object : Runnable {
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
        gameHandler = Handler() // Renamed from 'handler'
    }

    override fun onDraw(canvas: Canvas) {
        background?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        canvas.drawText("Pt: $points", 0f, TEXT_SIZE.toFloat(), scorePaint)
        for (i in life downTo 1) {
            lifeImage?.let {
                canvas.drawBitmap(it, screenWidth - it.width * i.toFloat(), 0f, null)
            }
        }
        if (life == 0) {
            paused = true
            gameHandler = null // Changed from 'handler'
            val intent = Intent(context, GameOver::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }

        enemySpaceship?.let { enemyShip ->
            enemyShip.ex += enemyShip.enemyVelocity
            if (enemyShip.ex + enemyShip.getEnemySpaceshipWidth() >= screenWidth) {
                enemyShip.enemyVelocity *= -1
            }

            if (enemyShip.ex <= 0) {
                enemyShip.enemyVelocity *= -1
            }

            if (!enemyShotAction && (enemyShip.ex >= 200 + random!!.nextInt(400))) {
                val enemyShot = Shot(context, enemyShip.ex + enemyShip.getEnemySpaceshipWidth() / 2, enemyShip.ey)
                enemyShots.add(enemyShot)
                enemyShotAction = true
            }
        }

        if (!paused) {
            handler?.postDelayed(runnable, UPDATE_MILLIS)
        }
        if (!enemyExplosion) {
            enemySpaceship?.getEnemySpaceshipBitmap()?.let { enemyBitmap ->
                canvas.drawBitmap(enemyBitmap, enemySpaceship!!.ex.toFloat(), enemySpaceship!!.ey.toFloat(), null)
            }
        }

        if (ourSpaceship!!.isAlive) {
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
            if ((ourShots[i].shx >= enemySpaceship!!.ex) && (ourShots[i].shx <= enemySpaceship!!.ex + enemySpaceship!!.getEnemySpaceshipWidth()) && (ourShots[i].shy <= enemySpaceship!!.getEnemySpaceshipHeight()) && (ourShots[i].shy >= enemySpaceship!!.ey)) {
                points++
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
                ourSpaceship!!.ox = touchX
            }
        }
        return true
    }
}
