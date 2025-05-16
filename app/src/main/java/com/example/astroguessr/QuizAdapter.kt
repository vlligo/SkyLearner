package com.example.astroguessr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuizAdapter(
    private val quizzes: List<Quiz>,
    private val scores: Map<String, Int>,  // Added scores parameter
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

                // Display score if available
                findViewById<TextView>(R.id.quizScore).text =
                    context.getString(R.string.best_score, scores[quiz.id] ?: 0)

                findViewById<TextView>(R.id.topics).text =
                    context.getString(R.string.topics_list, quiz.topics.joinToString(", "))

                setOnClickListener { onItemClick(quiz) }
            }
        }
    }
}