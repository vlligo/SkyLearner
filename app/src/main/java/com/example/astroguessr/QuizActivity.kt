package com.example.astroguessr

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    private lateinit var currentQuiz: Quiz
    private lateinit var nextButton: Button
    private lateinit var questionText: TextView
    private lateinit var progressBar: ProgressBar
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Modern Parcelable API with class parameter
        currentQuiz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_QUIZ", Quiz::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SELECTED_QUIZ")
        } ?: run {
            finish()
            return
        }

        // Initialize views once
        progressBar = findViewById(R.id.progressBar)
        questionText = findViewById(R.id.questionText)
        nextButton = findViewById(R.id.nextButton)

        setupQuestion()

        nextButton.setOnClickListener {
            if (currentQuestionIndex < currentQuiz.questionsCount - 1) {
                currentQuestionIndex++
                setupQuestion()
            } else {
                Toast.makeText(this, getString(R.string.quiz_completed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupQuestion() {
        questionText.text = getString(
            R.string.question_progress,
            currentQuestionIndex + 1,
            currentQuiz.questionsCount
        )
        progressBar.progress =
            ((currentQuestionIndex + 1) * 100) / currentQuiz.questionsCount
    }
}