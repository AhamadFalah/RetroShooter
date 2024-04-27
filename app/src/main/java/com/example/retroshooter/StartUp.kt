package com.example.retroshooter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.retroshooter.MainActivity

class StartUp : AppCompatActivity() {
    private lateinit var playerName: String
    private lateinit var etName: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)

        etName = findViewById(R.id.etName)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        playerName = intent.getStringExtra("playerName") ?: ""
    }

    fun startGame(v: View) {
        val playerName = etName.text.toString().trim()
        if (playerName.isNotEmpty()) {
            savePlayerName(playerName)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
            finish()
        } else {
            etName.error = "Please enter your name"
        }
    }

    private fun savePlayerName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("playerName", name)
        editor.apply()
    }
}
