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
    private var playerRocket: PlayerRocket? = null
    private var alienSpacecraft: AlienSpacecraft? = null
    private var random: Random? = null
    private var alienShots: ArrayList<Shot> = ArrayList()
    private var ourShots: ArrayList<Shot> = ArrayList()
    private var enemyExplosion = false
    private var explosion: Explosion? = null
    private var explosions: ArrayList<Explosion> = ArrayList()
    private var alienShotAction = false
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
        playerRocket = PlayerRocket(context)
        alienSpacecraft = AlienSpacecraft(context)
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

        alienSpacecraft?.let { alienSpaceCraft ->
            alienSpaceCraft.ex += alienSpaceCraft.alienVelocity
            if (alienSpaceCraft.ex + alienSpaceCraft.getAlienSpacecraftWidth() >= screenWidth) {
                alienSpaceCraft.alienVelocity *= -1
            }

            if (alienSpaceCraft.ex <= 0) {
                alienSpaceCraft.alienVelocity *= -1
            }

            if (!alienShotAction && (alienSpaceCraft.ex >= 200 + random!!.nextInt(400 - points / increaseShootingFrequencyAt * 100))) {
                val enemyShot = Shot(context, alienSpaceCraft.ex + alienSpaceCraft.getAlienSpacecraftWidth() / 2, alienSpaceCraft.ey)
                alienShots.add(enemyShot)
                alienShotAction = true
            }
        }

        if (points % increaseShootingFrequencyAt == 0) {
            increaseShootingFrequencyAt += 10 // Adjust this value as needed
            alienSpacecraft?.increaseShootingFrequency()
            alienSpacecraft?.increaseSpeed()
        }

        if (!paused) {
            gameHandler?.postDelayed(runnable, updateMillis)
        }
        if (!enemyExplosion) {
            alienSpacecraft?.getAlienSpacecraftBitmap()?.let { enemyBitmap ->
                canvas.drawBitmap(enemyBitmap, alienSpacecraft!!.ex.toFloat(), alienSpacecraft!!.ey.toFloat(), null)
            }
        }

        if (playerRocket?.isAlive == true) {
            if (playerRocket!!.ox > screenWidth - playerRocket!!.getPlayerRocketWidth()) {
                playerRocket!!.ox = screenWidth - playerRocket!!.getPlayerRocketWidth()
            } else if (playerRocket!!.ox < 0) {
                playerRocket!!.ox = 0
            }
            playerRocket?.getPlayerRocketBitmap()?.let { ourBitmap ->
                canvas.drawBitmap(ourBitmap, playerRocket!!.ox.toFloat(), playerRocket!!.oy.toFloat(), null)
            }
        }

        var i = 0
        while (i < alienShots.size) {
            alienShots[i].shy += 15
            alienShots[i].getShot()?.let {
                canvas.drawBitmap(it, alienShots[i].shx.toFloat(), alienShots[i].shy.toFloat(), null)
            }
            if ((alienShots[i].shx >= playerRocket!!.ox) && (alienShots[i].shx <= playerRocket!!.ox + playerRocket!!.getPlayerRocketWidth()) && (alienShots[i].shy >= playerRocket!!.oy) && (alienShots[i].shy <= screenHeight)) {
                life--
                alienShots.removeAt(i)
                explosion = Explosion(context, playerRocket!!.ox, playerRocket!!.oy)
                explosions.add(explosion!!)
            } else if (alienShots[i].shy >= screenHeight) {
                alienShots.removeAt(i)
            }
            if (alienShots.isEmpty()) {
                alienShotAction = false
            }
            i++
        }

        i = 0
        while (i < ourShots.size) {
            ourShots[i].shy -= 15
            ourShots[i].getShot()?.let {
                canvas.drawBitmap(it, ourShots[i].shx.toFloat(), ourShots[i].shy.toFloat(), null)
            }
            if ((ourShots[i].shx >= alienSpacecraft!!.ex) && (ourShots[i].shx <= alienSpacecraft!!.ex + alienSpacecraft!!.getAlienSpacecraftWidth()) && (ourShots[i].shy <= alienSpacecraft!!.ey + alienSpacecraft!!.getAlienSpacecraftHeight()) && (ourShots[i].shy >= alienSpacecraft!!.ey)) {
                points++
                if (points % increaseShootingFrequencyAt == 0) {
                    increaseShootingFrequencyAt += 10 // Adjust this value as needed
                }
                ourShots.removeAt(i)
                explosion = Explosion(context, alienSpacecraft!!.ex, alienSpacecraft!!.ey)
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
                    val ourShot = Shot(context, playerRocket!!.ox + playerRocket!!.getPlayerRocketWidth() / 2, playerRocket!!.oy)
                    ourShots.add(ourShot)
                }
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                playerRocket?.ox = touchX
            }
        }
        return true
    }
}