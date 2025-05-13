package com.example.astroguessr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizSpecsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_specs)

        val quiz = intent.getParcelableExtra<Quiz>("SELECTED_QUIZ") ?: return

        // Initialize views properly
        findViewById<TextView>(R.id.quizTitle).text = quiz.title
        findViewById<TextView>(R.id.quizDescription).text = quiz.description
        findViewById<TextView>(R.id.questionsCount).text = "Questions: ${quiz.questionsCount}"
        findViewById<TextView>(R.id.topics).text = "Spec: ${quiz.topics.joinToString(", ")}"

        findViewById<Button>(R.id.startQuizButton).setOnClickListener {
            Intent(this, QuizActivity::class.java).apply {
                putExtra("SELECTED_QUIZ", quiz)
                startActivity(this)
            }
        }
    }
}