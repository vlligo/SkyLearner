package com.example.astroguessr

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class UserProgress(
    var userId: String = "",
    var quizScores: Map<String, Int> = emptyMap()
) {
    @Exclude
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "quizScores" to quizScores
    )

    // empty constructor for Firestore
    constructor() : this("", emptyMap())
}