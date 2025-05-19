package com.example.astroguessr

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
data class UserProgress(
    var userId: String = "",
    var quizScores: Map<String, Int> = emptyMap()
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "quizScores" to quizScores
    )

    // empty constructor for Firestore
    constructor() : this("", emptyMap())
}