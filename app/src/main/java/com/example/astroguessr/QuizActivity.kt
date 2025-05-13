package com.example.astroguessr

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
        currentQuiz = intent.getParcelableExtra("SELECTED_QUIZ", Quiz::class.java) ?: run {
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
                Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupQuestion() {
        // Use already initialized views
        questionText.text = "Question ${currentQuestionIndex + 1}"
        progressBar.progress =
            ((currentQuestionIndex + 1) * 100) / currentQuiz.questionsCount
    }
}