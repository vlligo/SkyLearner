package com.example.astroguessr

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class QuizActivity : AppCompatActivity() {
    private lateinit var currentQuiz: Quiz
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nextButton: Button
    private lateinit var questionText: TextView
    private lateinit var progressBar: ProgressBar
    private var correctAnswers = 0
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Initialize views
        nextButton = findViewById(R.id.nextButton)
        questionText = findViewById(R.id.questionText)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        currentQuiz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_QUIZ", Quiz::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SELECTED_QUIZ")
        } ?: run {
            finish()
            return
        }

        setupQuestion()

        nextButton.setOnClickListener {
            checkAnswer() // Check answer before moving to next question
            if (currentQuestionIndex < currentQuiz.questions.size - 1) {
                currentQuestionIndex++
                setupQuestion()
            } else {
                saveProgress()
                finish()
            }
        }
    }

    private fun setupQuestion() {
        questionText.text = getString(
            R.string.question_progress,
            currentQuestionIndex + 1,
            currentQuiz.questions.size
        )
        progressBar.progress =
            ((currentQuestionIndex + 1) * 100) / currentQuiz.questions.size
    }

    private fun checkAnswer() {
        // Implement actual answer verification logic here
        val isCorrect = true // Replace with real validation
        if (isCorrect) correctAnswers++
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