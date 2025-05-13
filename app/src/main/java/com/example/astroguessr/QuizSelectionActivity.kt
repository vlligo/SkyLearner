package com.example.astroguessr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class QuizSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        val quizzes = listOf(
            Quiz(
                1,
                "Bayer Codes",
                "Matching Bayer Codes for constellations with their full names",
                15,
                listOf("Northern Hemisphere")
            ),
            Quiz(
                1,
                "Bayer Codes",
                "Matching Bayer Codes for constellations with their full names",
                15,
                listOf("Southern Hemisphere")
            )
        )

        try {
            val recyclerView = findViewById<RecyclerView>(R.id.quizzesRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = QuizAdapter(quizzes) { selectedQuiz ->
                Intent(this, QuizSpecsActivity::class.java).apply {
                    putExtra("SELECTED_QUIZ", selectedQuiz)
                    startActivity(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()  // Close activity if setup fails
        }
    }
}

class QuizAdapter(
    private val quizzes: List<Quiz>,
    private val onItemClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizzes[position])
    }

    override fun getItemCount() = quizzes.size

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(quiz: Quiz) {
            itemView.apply {
                findViewById<TextView>(R.id.quizTitle).text = quiz.title
                findViewById<TextView>(R.id.quizDescription).text = quiz.description
                findViewById<TextView>(R.id.topics).text =
                    context.getString(R.string.topics_list, quiz.topics.joinToString(", "))
                setOnClickListener { onItemClick(quiz) }
            }
        }
    }
}