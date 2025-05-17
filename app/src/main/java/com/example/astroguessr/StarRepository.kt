package com.example.astroguessr

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await

class StarRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getStarsByConstellation(constellation: String): List<Star> {
        return try {
            db.collection("stars")
                .whereEqualTo("con", constellation)
                .get()
                .await()
                .map { it.toStar() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun QueryDocumentSnapshot.toStar(): Star {
        val geoPoint = getGeoPoint("coordinates") ?: GeoPoint(0.0, 0.0)
        return Star(
            id = id,
            bf = getString("bf") ?: "",
            proper = getString("proper") ?: "",
            ra = getDouble("ra") ?: 0.0,
            dec = getDouble("dec") ?: 0.0,
            mag = getDouble("mag") ?: 0.0,
            bayer = getString("bayer") ?: "",
            con = getString("con") ?: "",
            latitude = geoPoint.latitude,
            longitude = geoPoint.longitude
        )
    }
}