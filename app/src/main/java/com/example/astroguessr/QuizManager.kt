package com.example.astroguessr

import android.util.Log
import kotlin.random.Random

class QuizManager(private val starRepository: StarRepository) {

    suspend fun generateQuizFromFirebase(firebaseQuiz: Quiz): Quiz {
        val validQuestions = mutableListOf<Question>()

        firebaseQuiz.questions.forEach { question ->
            try {
                val targetStar = starRepository.getStarById(question.targetStarId)
                    ?: throw Exception("Target star ${question.targetStarId} not found")

                val nearbyStars = starRepository.getStarsNear(
                    ra = targetStar.ra + Random.nextFloat() * 20f - 10f,
                    dec = targetStar.dec + Random.nextFloat() * 20f - 10f,
                    radius = 30.0,
                    excludeId = targetStar.id,
                    count = 900
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