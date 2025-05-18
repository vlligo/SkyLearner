package com.example.astroguessr

import com.example.astroguessr.data.Star

class QuizManager(private val starRepository: StarRepository) {

    suspend fun generateConstellationQuiz(constellation: String): Quiz {
        val stars = starRepository.getStarsByConstellation(constellation)
        val questions = stars.take(10).map { star ->
            Question(
                targetStarId = star.id,
                optionIds = createOptionIds(star.id),
                constellation = constellation
            )
        }
        return Quiz(
            id = "constellation_${constellation.lowercase()}",
            title = "Identify Stars in ${constellation.uppercase()}",
            description = "Find the correct stars in $constellation",
            questions = questions,
            topics = listOf(constellation)
        )
    }

    private suspend fun createOptionIds(correctId: Int): List<Int> {
        val wrongOptions = starRepository.getRandomStars(3, correctId).map { it.id }
        return (listOf(correctId) + wrongOptions).shuffled()
    }
}