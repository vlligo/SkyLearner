package com.example.astroguessr

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question> = emptyList(), // Now contains actual questions
    val topics: List<String>
) : Parcelable

@Parcelize
data class Question(
    val targetStar: Star,
    val options: List<Star>
) : Parcelable