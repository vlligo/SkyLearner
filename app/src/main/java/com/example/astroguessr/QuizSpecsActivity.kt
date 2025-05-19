package com.example.astroguessr

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class QuizSpecsActivity : AppCompatActivity() {
    @SuppressLint("NewApi", "Deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_specs)

        val quiz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_QUIZ", Quiz::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SELECTED_QUIZ")
        } ?: run {
            finish()
            return
        }

        findViewById<TextView>(R.id.quizTitle).text = quiz.title
        findViewById<TextView>(R.id.quizDescription).text = quiz.description
        findViewById<TextView>(R.id.questionsCount).text =
            getString(R.string.questions_count, quiz.questions.size)

        findViewById<Button>(R.id.startQuizButton).setOnClickListener {
            if (quiz.questions.isEmpty()) {
                Toast.makeText(this, "No valid questions!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Intent(this, QuizActivity::class.java).apply {
                val shuffledQuiz = quiz.copy(questions = quiz.questions.shuffled())
                putExtra("SELECTED_QUIZ", shuffledQuiz)
                startActivity(this)
            }
        }
    }
}