package com.example.retroshooter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NameEntryActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        etName = findViewById(R.id.etName)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val playerName = etName.text.toString().trim()
            if (playerName.isNotEmpty()) {
                val intent = Intent(this, StartUp::class.java)
                intent.putExtra("playerName", playerName)
                startActivity(intent)
                finish()
            } else {
                etName.error = "Please enter your name"
            }
        }
    }
}

