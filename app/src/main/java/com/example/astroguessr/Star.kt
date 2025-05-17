package com.example.astroguessr

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class Star(
    val id: String = "",       // Firestore document ID
    val bf: String = "",       // Bayer/Flamsteed designation
    val proper: String = "",   // Proper name
    val ra: Double = 0.0,      // Right Ascension
    val dec: Double = 0.0,     // Declination
    val mag: Double = 0.0,     // Apparent magnitude
    val bayer: String = "",    // Bayer designation
    val con: String = "",      // Constellation
    val latitude: Double = 0.0, // For GeoPoint conversion
    val longitude: Double = 0.0 // For GeoPoint conversion
) : Parcelable {
    fun toGeoPoint() = GeoPoint(latitude, longitude)
}
