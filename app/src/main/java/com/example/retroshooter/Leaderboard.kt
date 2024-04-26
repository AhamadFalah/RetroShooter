package com.example.retroshooter

import ScoreManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Leaderboard : AppCompatActivity() {
    private lateinit var lvLeaderboard: ListView
    private lateinit var scoreManager: ScoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard)

        lvLeaderboard = findViewById(R.id.lvLeaderboard)
        scoreManager = ScoreManager(this)

        val leaderboardEntries = mutableListOf<String>()
        val allScores = scoreManager.getAllScores()
        for ((playerName, score) in allScores) {
            leaderboardEntries.add("$playerName: $score")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, leaderboardEntries)
        lvLeaderboard.adapter = adapter
    }
}