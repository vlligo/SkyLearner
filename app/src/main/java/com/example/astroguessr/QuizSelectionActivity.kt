package com.example.astroguessr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking

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

    private fun loadQuizzes(scores: Map<String, Int>) {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.toObjects(Quiz::class.java)
//                setupAdapter(quizzes, scores)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading quizzes: ${e.message}", Toast.LENGTH_SHORT).show()
                setupAdapter(emptyMap())
            }
    }

    private fun setupAdapter(scores: Map<String, Int>) {
        val quizzes = listOf(
            Quiz(
                id = "constellations_1",
                title = "Northern Constellations",
                description = "Identify stars in northern hemisphere constellations",
                topics = listOf("Northern"),
                questions = generateQuestionsForConstellation("UMa") // Ursa Major example
            )
        )


        val recyclerView = findViewById<RecyclerView>(R.id.quizzesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuizAdapter(
            quizzes = quizzes,
            scores = scores,
            onItemClick = { selectedQuiz: Quiz ->  // Add explicit type here
                Intent(this, QuizSpecsActivity::class.java).apply {
                    putExtra("SELECTED_QUIZ", selectedQuiz)
                    startActivity(this)
                }
            }
        )

    }
    private fun generateQuestionsForConstellation(constellation: String): List<Question> {
        val repo = StarRepository()
        val stars = runBlocking { repo.getStarsByConstellation(constellation) }

        return stars.take(10).map { star ->
            Question(
                targetStar = star,
                options = listOf(star) + stars.filterNot { it.id == star.id }.shuffled().take(3)
            )
        }
    }
}
