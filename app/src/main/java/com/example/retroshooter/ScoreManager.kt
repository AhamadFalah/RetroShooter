package com.example.retroshooter

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class ScoreManager(context: Context) {
    private val databaseHelper = DatabaseHelper(context)

    fun saveScore(playerName: String, score: Int) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PLAYER_NAME, playerName)
            put(DatabaseHelper.COLUMN_SCORE, score)
        }
        db.insert(DatabaseHelper.TABLE_SCORES, null, values)
        db.close()
    }

    fun getAllScores(): Map<String, Int> {
        val db = databaseHelper.readableDatabase
        val scores = mutableMapOf<String, Int>()
        val cursor = db.query(
            DatabaseHelper.TABLE_SCORES,
            arrayOf(DatabaseHelper.COLUMN_PLAYER_NAME, DatabaseHelper.COLUMN_SCORE),
            null, null, null, null,
            "${DatabaseHelper.COLUMN_SCORE} DESC"
        )
        while (cursor.moveToNext()) {
            val playerName = cursor.getString(0)
            val score = cursor.getInt(1)
            scores[playerName] = score
        }
        cursor.close()
        db.close()
        return scores
    }
}