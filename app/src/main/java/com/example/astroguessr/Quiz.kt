package com.example.astroguessr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question>,
    val topics: List<String>
) : Parcelable

@Parcelize
data class Question(
    val targetStarId: Int,  // Store Star ID instead of the full object
    val optionIds: List<Int>,  // List of Star IDs
    val constellation: String
) : Parcelable