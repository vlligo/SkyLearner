package com.example.astroguessr

data class UserProgress(
    val userId: String = "",
    val quizScores: Map<String, Int> = emptyMap() // Key: quizId, Value: best score percentage
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "quizScores" to quizScores
    )
}