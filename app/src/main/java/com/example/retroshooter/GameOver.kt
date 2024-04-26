package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        val points = intent.getIntExtra("points", 0)
        val tvPoints = findViewById<TextView>(R.id.tvPoints)
        tvPoints.text = points.toString()
    }

    fun restart(v: View) {
        val intent = Intent(this, StartUp::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(v: View) {
        finish()
    }

    fun showLeaderboard(v: View) {
        val intent = Intent(this, Leaderboard::class.java)
        startActivity(intent)
    }
}