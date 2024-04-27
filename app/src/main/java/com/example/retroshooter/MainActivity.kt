package com.example.retroshooter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playerName: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.retro)

        playerName = intent.getStringExtra("playerName") ?: ""
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        val retroShooter = findViewById<RetroShooter>(R.id.retroShooter)
        val points = retroShooter.points

        // Save player name and points to SharedPreferences
        savePlayerData(playerName, points)
        mediaPlayer.release()
        val intent = Intent(this, GameOver::class.java)
        intent.putExtra("points", points)
        startActivity(intent)
        finish()
    }

    private fun savePlayerData(playerName: String, points: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("playerName", playerName)
        editor.putInt("playerPoints", points)
        editor.apply()
    }
}
