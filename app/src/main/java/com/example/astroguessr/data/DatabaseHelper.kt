package com.example.astroguessr.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) {
    private val db = AppDatabase.create(context)

    suspend fun getStarsByConstellation(constellation: String): List<Star> {
        return withContext(Dispatchers.IO) {
            db.starDao().getStarsByConstellation(constellation)
        }
    }

    suspend fun getRandomQuestion(): Star {
        return withContext(Dispatchers.IO) {
            db.starDao().getRandomStars(1, 0).first()
        }
    }

    suspend fun getWrongAnswers(correctId: Int, count: Int = 3): List<Star> {
        return withContext(Dispatchers.IO) {
            db.starDao().getRandomStars(count, correctId)
        }
    }
}