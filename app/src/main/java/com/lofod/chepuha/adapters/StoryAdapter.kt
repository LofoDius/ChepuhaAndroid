package com.lofod.chepuha.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.lofod.chepuha.R
import com.lofod.chepuha.model.Answer
import com.pranavpandey.android.dynamic.toasts.DynamicToast

class StoryAdapter(private val story: MutableList<Answer>) : RecyclerView.Adapter<StoryAdapter.StoryLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryLineViewHolder {
        return StoryLineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_story_line, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoryLineViewHolder, position: Int) {
        holder.bind(story[position])
    }

    override fun getItemCount(): Int {
        return story.size
    }

    class StoryLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var author = """Автор неизвестен 😞"""

        fun bind(answer: Answer) {
            val answerTextView = itemView.findViewById<TextView>(R.id.answer)
            answerTextView.text = answer.text
            author = answer.author

            itemView.setOnLongClickListener {
                val context = itemView.context
                DynamicToast.make(
                    context,
                    author,
                    AppCompatResources.getDrawable(context, R.drawable.ic_info_toast),
                    Color.WHITE,
                    Color.valueOf(166F, 200F, 227F).toArgb(),
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }
}