package com.example.astroguessr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
            setupAdapter(emptyMap())
            return
        }

        db.collection("user_progress")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val progress = document.toObject(UserProgress::class.java)
                setupAdapter(progress?.quizScores ?: emptyMap())
            }
    }

    private fun setupAdapter(scores: Map<String, Int>) {
        val quizzes = listOf(
            Quiz(
                "1",  // Must be unique ID
                "Bayer Codes",
                "Matching Bayer Codes for constellations...",
                15,
                listOf("Northern Hemisphere")
            ),
            Quiz(
                "2",
                "Bayer Codes",
                "Matching Bayer Codes for constellations...",
                15,
                listOf("Southern Hemisphere")
            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.quizzesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuizAdapter(quizzes, scores) { selectedQuiz ->
            Intent(this, QuizSpecsActivity::class.java).apply {
                putExtra("SELECTED_QUIZ", selectedQuiz)
                startActivity(this)
            }
        }
    }
}
