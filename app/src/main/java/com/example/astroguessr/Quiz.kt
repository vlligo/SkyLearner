package com.example.astroguessr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val questionsCount: Int,
    val topics: List<String>
) : Parcelable