package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Leaderboard : AppCompatActivity() {
    private lateinit var lvLeaderboard: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard)

        lvLeaderboard = findViewById(R.id.lvLeaderboard)

        val leaderboardEntries = mutableListOf<String>()
        val allScores = RetroShooterApplication.scoreManager.getAllScores()
        val highestScorePair = RetroShooterApplication.scoreManager.getHighScorePlayerName()

        for ((playerName, score) in allScores) {
            val entry = if (playerName == highestScorePair.first) {
                // Highlight highest score entry
                "**$playerName: $score**"
            } else {
                "$playerName: $score"
            }
            leaderboardEntries.add(entry)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, leaderboardEntries)
        lvLeaderboard.adapter = adapter
    }

    fun back(v: View) {
        val intent = Intent(this, StartUp::class.java)
        startActivity(intent)
        finish()
    }
}