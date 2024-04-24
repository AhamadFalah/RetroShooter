package com.example.retroshooter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.content.Intent


class StartUp : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)
    }

    fun startGame(v: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}