package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var playerName: String
    private var playerScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        playerName = sharedPreferences.getString("playerName", "") ?: ""
        playerScore = intent.getIntExtra("points", 0)

        // Save score and player name using ScoreManager
        RetroShooterApplication.scoreManager.saveScore(playerName, playerScore)

        val tvHighScore = findViewById<TextView>(R.id.tvHighScore)
        val tvYourScore = findViewById<TextView>(R.id.tvYourScore)

        // Retrieve and display high score from ScoreManager
        val highScorePair = RetroShooterApplication.scoreManager.getHighScorePlayerName()
        tvHighScore.text = "High Score: ${highScorePair.second} by ${highScorePair.first}"

        // Display player's score
        tvYourScore.text = "Your Score: $playerScore by $playerName"
    }

    fun restart(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(v: View) {
        val intent = Intent(this, StartUp::class.java)
        startActivity(intent)
        finish()
    }

    fun showLeaderboard(v: View) {
        val intent = Intent(this, Leaderboard::class.java)
        startActivity(intent)
    }
}