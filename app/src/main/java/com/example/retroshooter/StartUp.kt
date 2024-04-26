package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class StartUp : AppCompatActivity() {
    private lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)

        playerName = intent.getStringExtra("playerName") ?: ""
        if (playerName.isEmpty()) {
            startActivity(Intent(this, NameEntryActivity::class.java))
            finish()
        }
    }

    fun startGame(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("playerName", playerName)
        startActivity(intent)
        finish()
    }
}