package com.example.astroguessr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class QuizSelectionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadProgress()
    }

    private fun loadProgress() {
        val user = auth.currentUser ?: run {
            setupAdapter(emptyList(), emptyMap())
            return
        }

        db.collection("user_progress")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val progress = document.toObject(UserProgress::class.java)
                loadQuizzes(progress?.quizScores ?: emptyMap())
            }
    }

    private fun loadQuizzes(scores: Map<String, Int>) {
        lifecycleScope.launch {
            try {
                val quizManager = QuizManager(StarRepository(this@QuizSelectionActivity))
                val constellationQuiz = quizManager.generateConstellationQuiz("CMa")
                if (constellationQuiz.questions.isEmpty()) {
                    Toast.makeText(
                        this@QuizSelectionActivity,
                        "No questions found!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setupAdapter(listOf(constellationQuiz), scores)
            } catch (e: Exception) {
                e.message?.let { Log.e("TAG", it) }
                Toast.makeText(
                    this@QuizSelectionActivity,
                    "Failed to load quizzes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupAdapter(quizzes: List<Quiz>, scores: Map<String, Int>) {
        if (quizzes.isEmpty()) {
            Toast.makeText(this, "No quizzes available!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.quizzesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@QuizSelectionActivity)
        recyclerView.adapter = QuizAdapter(
            quizzes = quizzes,
            scores = scores,
            onItemClick = { selectedQuiz ->
                Intent(this@QuizSelectionActivity, QuizSpecsActivity::class.java).apply {
                    putExtra("SELECTED_QUIZ", selectedQuiz)
                    startActivity(this)
                }
            }
        )
    }
}