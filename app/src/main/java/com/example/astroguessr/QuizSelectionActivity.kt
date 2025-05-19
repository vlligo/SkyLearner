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
import kotlinx.coroutines.tasks.await

class QuizSelectionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var starRepository: StarRepository
    private lateinit var quizManager: QuizManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        starRepository = StarRepository(this)
        quizManager = QuizManager(starRepository)

        loadProgress()
    }

    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_quiz_selection)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        starRepository = StarRepository(this)
        quizManager = QuizManager(starRepository)

        loadProgress()
    }

    private fun loadProgress() {
        val user = auth.currentUser ?: run {
            showQuizzes(emptyList(), emptyMap())
            return
        }

        lifecycleScope.launch {
            try {
                val document = db.collection("user_progress")
                    .document(user.uid)
                    .get()
                    .await()

                val progress = document.toObject(UserProgress::class.java)
                loadQuizzes(progress?.quizScores ?: emptyMap())
            } catch (e: Exception) {
                Log.e("QuizSelection", "Progress load failed", e)
                runOnUiThread {
                    Toast.makeText(
                        this@QuizSelectionActivity,
                        "Failed to load progress: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loadQuizzes(emptyMap())
            }
        }
    }

    private fun loadQuizzes(scores: Map<String, Int>) {
        lifecycleScope.launch {
            try {
                Log.d("Firestore", "Attempting to load quizzes...")

                val result = db.collection("quizzes")
                    .get()
                    .await()

                Log.d("Firestore", "Query completed. Documents: ${result.size()}")

                if (result.isEmpty) {
                    Log.w("Firestore", "No quizzes found in collection")
                    showErrorToast("No quizzes available in database")
                    return@launch
                }

                val firebaseQuizzes = result.toObjects(Quiz::class.java).also {
                    Log.d("Firestore", "Deserialized ${it.size} quizzes")
                }
                val validQuizzes = mutableListOf<Quiz>()

                firebaseQuizzes.forEach { firebaseQuiz ->
                    try {
                        val fullQuiz = quizManager.generateQuizFromFirebase(firebaseQuiz)
                        if (fullQuiz.questions.isNotEmpty()) {
                            validQuizzes.add(fullQuiz)
                        }
                    } catch (e: Exception) {
                        Log.e("QuizSelection", "Failed to process quiz ${firebaseQuiz.id}", e)
                    }
                }

                if (validQuizzes.isEmpty()) {
                    runOnUiThread {
                        Toast.makeText(
                            this@QuizSelectionActivity,
                            "No valid quizzes found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                showQuizzes(validQuizzes, scores)

            } catch (e: Exception) {
                Log.e("FirestoreError", "Load failed", e)
                showErrorToast("Network error: ${e.localizedMessage}")
                Log.e("QuizSelection", "Quiz load failed", e)
                runOnUiThread {
                    Toast.makeText(
                        this@QuizSelectionActivity,
                        "Failed to load quizzes: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showQuizzes(emptyList(), scores)
            }
        }
    }

    private fun showErrorToast(message: String) {
        runOnUiThread {
            Toast.makeText(
                this@QuizSelectionActivity,
                message,
                Toast.LENGTH_LONG  // Longer duration for debugging
            ).show()
        }
    }

    private fun showQuizzes(quizzes: List<Quiz>, scores: Map<String, Int>) {
        runOnUiThread {
            val recyclerView = findViewById<RecyclerView>(R.id.quizzesRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@QuizSelectionActivity)
            recyclerView.adapter = QuizAdapter(
                quizzes = quizzes,
                scores = scores,
                onItemClick = { selectedQuiz ->
                    startActivity(
                        Intent(this@QuizSelectionActivity, QuizSpecsActivity::class.java).apply {
                            putExtra("SELECTED_QUIZ", selectedQuiz)
                        }
                    )
                }
            )
        }
    }
}