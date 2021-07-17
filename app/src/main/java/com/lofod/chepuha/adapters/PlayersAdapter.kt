package com.lofod.chepuha.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lofod.chepuha.R
import com.lofod.chepuha.model.Player

class PlayersAdapter(private var playerName: String, private var players: MutableList<Player>) :
    RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_player, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val name = players[position].name
        holder.bind(name, name == playerName)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(roomMemberName: String, isShowingAvatar: Boolean) {
            val name = itemView.findViewById<TextView>(R.id.playerName)
            name.text = roomMemberName

            if (!isShowingAvatar) {
                name.setCompoundDrawables(null, null, null, null)
            }
        }

    }
}