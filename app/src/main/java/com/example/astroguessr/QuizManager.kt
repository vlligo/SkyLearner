package com.example.astroguessr

import com.example.astroguessr.data.Star
import kotlin.math.abs
import android.util.Log

class QuizManager(private val starRepository: StarRepository) {

    suspend fun generateQuizFromFirebase(firebaseQuiz: Quiz): Quiz {
        val validQuestions = mutableListOf<Question>()

        firebaseQuiz.questions.forEach { question ->
            try {
                val targetStar = starRepository.getStarById(question.targetStarId)
                    ?: throw Exception("Target star ${question.targetStarId} not found")

                val nearbyStars = starRepository.getStarsNear(
                    ra = targetStar.ra,
                    dec = targetStar.dec,
                    radius = 2.0,
                    excludeId = targetStar.id,
                    count = 3
                )

                if (nearbyStars.size < 3) {
                    throw Exception("Not enough nearby stars for ${targetStar.name}")
                }

                validQuestions.add(
                    Question(
                        targetStarId = question.targetStarId,
                        options = (nearbyStars + targetStar).shuffled()
                    )
                )
            } catch (e: Exception) {
                Log.w("QuizManager", "Skipping invalid question: ${e.message}")
            }
        }

        if (validQuestions.isEmpty()) {
            throw Exception("No valid questions in quiz ${firebaseQuiz.id}")
        }

        return firebaseQuiz.copy(
            questions = validQuestions,
            // Ensure at least 1 question exists
            description = if (validQuestions.size < firebaseQuiz.questions.size) {
                "${firebaseQuiz.description} (${validQuestions.size} valid questions)"
            } else {
                firebaseQuiz.description
            }
        )
    }
}