package com.lofod.chepuha.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lofod.chepuha.R
import com.lofod.chepuha.model.Answer

class StoryAdapter(private val story: MutableList<Answer>) : RecyclerView.Adapter<StoryAdapter.StoryLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryLineViewHolder {
        return StoryLineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_story_line, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoryLineViewHolder, position: Int) {
        holder.bind(story[position], position)
    }

    override fun getItemCount(): Int {
        return story.size
    }

    class StoryLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var itemPosition = -1

        fun bind(answer: Answer, position: Int) {
            val answerTextView = itemView.findViewById<TextView>(R.id.answer)
            answerTextView.text = answer.text
            itemPosition = position
        }
    }
}