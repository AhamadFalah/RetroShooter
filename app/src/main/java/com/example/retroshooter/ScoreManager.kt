// ScoreManager.kt
package com.example.retroshooter

import DatabaseHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast

class ScoreManager(private val context: Context) {
    private val TAG = "ScoreManager"
    private val databaseHelper = DatabaseHelper.getInstance(context)

    fun saveScore(playerName: String, score: Int) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PLAYER_NAME, playerName)
            put(DatabaseHelper.COLUMN_SCORE, score)
        }

        val rowId = try {
            db.insert(DatabaseHelper.TABLE_SCORES, null, values)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving score: ${e.message}")
            showToast("Failed to save score")
            -1L
        }

        db.close()

        if (rowId != -1L) {
            Log.d(TAG, "Score saved successfully")
        } else {
            Log.e(TAG, "Failed to save score")
        }
    }

    fun getHighScore(): Int {
        val db = databaseHelper.readableDatabase
        var highScore = 0
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SCORES,
            arrayOf(DatabaseHelper.COLUMN_SCORE),
            null, null, null, null,
            "${DatabaseHelper.COLUMN_SCORE} DESC",
            "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                highScore = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
            }
        }
        db.close()
        Log.d(TAG, "High score retrieved: $highScore")
        return highScore
    }

    fun getHighScorePlayerName(): Pair<String, Int> {
        val db = databaseHelper.readableDatabase
        var playerName = ""
        var highScore = 0
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SCORES,
            arrayOf(DatabaseHelper.COLUMN_PLAYER_NAME, DatabaseHelper.COLUMN_SCORE),
            null, null, null, null,
            "${DatabaseHelper.COLUMN_SCORE} DESC",
            "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                playerName = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_NAME))
                highScore = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
            }
        }
        db.close()
        Log.d(TAG, "High score player name: $playerName, Score: $highScore")
        return Pair(playerName, highScore)
    }

    fun getAllScores(): Map<String, Int> {
        val db = databaseHelper.readableDatabase
        val scores = mutableMapOf<String, Int>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SCORES,
            arrayOf(DatabaseHelper.COLUMN_PLAYER_NAME, DatabaseHelper.COLUMN_SCORE),
            null, null, null, null,
            "${DatabaseHelper.COLUMN_SCORE} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val playerName = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_NAME))
                val score = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
                scores[playerName] = score
            }
        }
        db.close()
        Log.d(TAG, "All scores retrieved: $scores")
        return scores
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}