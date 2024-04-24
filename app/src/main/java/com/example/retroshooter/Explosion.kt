package com.example.retroshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(context: Context, var ex: Int, var ey: Int) {
    val explosion = arrayOfNulls<Bitmap>(9)
    var explosionFrame = 0

    init {
        explosion[0] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion8)
        explosion[1] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion4)
        explosion[2] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion7)
        explosion[3] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion3)
        explosion[4] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion5)
        explosion[5] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion6)
        explosion[6] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion2)
        explosion[7] = BitmapFactory.decodeResource(context.resources, R.drawable.explosion1)
        explosionFrame = 0
    }

    fun getExplosion(explosionFrame: Int): Bitmap? {
        return explosion[explosionFrame]
    }
}
