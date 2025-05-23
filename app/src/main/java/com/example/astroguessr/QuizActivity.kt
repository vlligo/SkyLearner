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
import kotlinx.coroutines.tasks.await
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
    private var isFeedbackPhase = false

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
            if (!isFeedbackPhase) {
                if (selectedStar == null) {
                    Toast.makeText(this, "Select a star first!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val currentQuestion = currentQuiz.questions[currentQuestionIndex]
                val isCorrect = selectedStar?.id == currentQuestion.targetStarId
                if (isCorrect) correctAnswers++

                // Highlight correct and selected stars
                starChart.showFeedback(
                    correctStarId = currentQuestion.targetStarId,
                    selectedStarId = selectedStar?.id
                )

                // Show correctness message
                val message = if (isCorrect) "Correct!" else "Incorrect. Correct star highlighted."
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Disable interactions during feedback
                starChart.isEnabled = false
                nextButton.text = getString(R.string.kontinue)
                isFeedbackPhase = true
            } else {
                starChart.showFeedback(null, null)
                starChart.isEnabled = true
                nextButton.text = getString(R.string.check)
                isFeedbackPhase = false

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
                    ?: throw Exception("Target star not found")

                withContext(Dispatchers.Main) {
                    // Wait for view layout before setting stars
                    starChart.post {
                        starChart.setStars(currentQuestion.options)
                        questionText.text =
                            getString(R.string.find, targetStar.name ?: targetStar.bayer)
                        progressBar.progress = ((currentQuestionIndex + 1) * 100) / currentQuiz.questions.size
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuizActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }


    override fun onStarSelected(star: Star) {
        selectedStar = star
    }

    private fun checkAnswer() {
        val currentQuestion = currentQuiz.questions[currentQuestionIndex]
        val isCorrect = selectedStar?.id == currentQuestion.targetStarId
        if (isCorrect) correctAnswers++
        selectedStar = null
    }

    private fun saveProgress() {
        val user = auth.currentUser ?: return
        val newScore = (correctAnswers * 100) / currentQuiz.questions.size
        val quizId = currentQuiz.id
        val userProgressRef = db.collection("user_progress").document(user.uid)

        lifecycleScope.launch {
            try {
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userProgressRef)

                    val currentScores = (snapshot.get("quizScores") as? Map<String, Long>)
                        ?.mapValues { it.value.toInt() }
                        ?: emptyMap()

                    val currentScore = currentScores[quizId] ?: 0
                    val max = maxOf(newScore, currentScore)

                    if (newScore > currentScore) {
                        val updatedScores = currentScores.toMutableMap().apply {
                            put(quizId, max)
                        }
                        transaction.set(
                            userProgressRef,
                            mapOf("quizScores" to updatedScores.mapValues { it.value.toLong() }),
                            SetOptions.merge()
                        )
                    }
                    max
                }.await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@QuizActivity,
                        "Score: $newScore%",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@QuizActivity,
                        "Save failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}