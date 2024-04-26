package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerName = intent.getStringExtra("playerName") ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        val retroShooter = findViewById<RetroShooter>(R.id.retroShooter)
        val points = retroShooter.points
        val intent = Intent(this, GameOver::class.java)
        intent.putExtra("points", points)
        startActivity(intent)
        finish()
    }
}