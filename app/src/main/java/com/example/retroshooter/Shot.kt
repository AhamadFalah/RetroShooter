package com.example.retroshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Shot(context: Context, var shx: Int, var shy: Int) {
    private var shot: Bitmap? = null

    init {
        shot = BitmapFactory.decodeResource(context.resources, R.drawable.laser)
    }

    fun getShot(): Bitmap? {
        return shot
    }
}
