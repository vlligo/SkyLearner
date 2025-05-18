package com.example.astroguessr

import android.content.Context
import com.example.astroguessr.data.AppDatabase
import com.example.astroguessr.data.Star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StarRepository(private val context: Context) {
    private val db = AppDatabase.create(context)

    suspend fun getStarsByConstellation(constellation: String): List<Star> {
        return withContext(Dispatchers.IO) {
            db.starDao().getStarsByConstellation(constellation)
        }
    }

    suspend fun getRandomStars(count: Int, excludeId: Int): List<Star> {
        return withContext(Dispatchers.IO) {
            db.starDao().getRandomStars(count, excludeId)
        }
    }

    suspend fun getStarById(id: Int): Star? {
        return withContext(Dispatchers.IO) {
            db.starDao().getStarById(id)
        }
    }

    suspend fun getStarsNear(
        ra: Double,
        dec: Double,
        radius: Double,
        excludeId: Int,
        count: Int
    ): List<Star> {
        return withContext(Dispatchers.IO) {
            db.starDao().getStarsNear(
                ra = ra,
                dec = dec,
                radius = radius,
                excludeId = excludeId,
                count = count
            )
        }
    }
}