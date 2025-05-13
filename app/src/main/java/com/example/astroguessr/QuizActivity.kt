package com.example.astroguessr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import android.widget.ProgressBar

class QuizActivity : AppCompatActivity() {
    private lateinit var currentQuiz: Quiz
    private lateinit var nextButton: Button
    private lateinit var questionText: TextView
    private lateinit var progressBar: ProgressBar
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        currentQuiz = intent.getParcelableExtra("SELECTED_QUIZ") ?: return
        setupQuestion()
        // Initialize views
        progressBar = findViewById(R.id.progressBar)
        questionText = findViewById(R.id.questionText)
        nextButton = findViewById(R.id.nextButton)

        nextButton.setOnClickListener {
            if (currentQuestionIndex < currentQuiz.questionsCount - 1) {
                currentQuestionIndex++
                setupQuestion()
            } else {
                // Handle quiz completion
                Toast.makeText(this@QuizActivity, "Quiz Completed!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupQuestion() {
        // Replace with actual questions from your data source
        findViewById<TextView>(R.id.questionText).text = "Question ${currentQuestionIndex + 1}"
        findViewById<ProgressBar>(R.id.progressBar).progress =
            (currentQuestionIndex + 1) * 100 / currentQuiz.questionsCount
    }
}