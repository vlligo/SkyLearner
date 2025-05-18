package com.example.astroguessr

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.astroguessr.data.Star
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity(), StarChartView.OnStarSelectedListener {
    private lateinit var currentQuiz: Quiz
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nextButton: Button
    private lateinit var questionText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var starChart: StarChartView
    private var correctAnswers = 0
    private var currentQuestionIndex = 0
    private var selectedStar: Star? = null
    private lateinit var starRepository: StarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        starRepository = StarRepository(this)

        // Initialize views
        starChart = findViewById(R.id.starChart)
        starChart.onStarSelectedListener = this
        nextButton = findViewById(R.id.nextButton)
        questionText = findViewById(R.id.questionText)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        currentQuiz = intent.getParcelableExtra("SELECTED_QUIZ") ?: run {
            finish()
            return
        }

        if (currentQuiz.questions.isEmpty()) {
            Toast.makeText(this, "No questions available!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupQuestion()

        // Updated click listener with coroutine
        nextButton.setOnClickListener {
            if (selectedStar == null) {
                Toast.makeText(this, "Select a star first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                checkAnswer() // Call suspend function
                if (currentQuestionIndex < currentQuiz.questions.size - 1) {
                    currentQuestionIndex++
                    setupQuestion()
                } else {
                    saveProgress()
                    finish()
                }
            }
        }
    }

    private fun setupQuestion() {
        lifecycleScope.launch {
            try {
                val currentQuestion = currentQuiz.questions[currentQuestionIndex]
                val targetStar = starRepository.getStarById(currentQuestion.targetStarId)
                val optionStars = currentQuestion.optionIds.mapNotNull { id ->
                    starRepository.getStarById(id)
                }

                withContext(Dispatchers.Main) {
                    if (targetStar == null || optionStars.isEmpty()) {
                        Toast.makeText(this@QuizActivity, "Error loading stars!", Toast.LENGTH_SHORT).show()
                        finish()
                        return@withContext
                    }

                    starChart.setStars(optionStars)
                    questionText.text = getString(
                        R.string.question_progress,
                        currentQuestionIndex + 1,
                        currentQuiz.questions.size
                    )
                    progressBar.progress = ((currentQuestionIndex + 1) * 100) / currentQuiz.questions.size
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuizActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStarSelected(star: Star) {
        selectedStar = star
    }

    // Marked as suspend
    private suspend fun checkAnswer() {
        val currentQuestion = currentQuiz.questions[currentQuestionIndex]
        val targetStar = starRepository.getStarById(currentQuestion.targetStarId)
        val isCorrect = selectedStar?.id == targetStar?.id
        if (isCorrect) correctAnswers++
        selectedStar = null
    }

    private fun saveProgress() {
        val user = auth.currentUser ?: return
        val score = (correctAnswers * 100) / currentQuiz.questions.size

        db.collection("user_progress")
            .document(user.uid)
            .set(mapOf("quizScores.${currentQuiz.id}" to score), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Progress saved! Score: $score%", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}