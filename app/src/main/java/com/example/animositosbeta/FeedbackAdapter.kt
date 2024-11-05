package com.example.animositosbeta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedbackAdapter(private val feedbackList: List<Feedback>) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentTextView: TextView = itemView.findViewById(R.id.commentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.commentTextView.text = feedback.comment
        holder.ratingBar.rating = feedback.rating.toFloat()
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }
}
