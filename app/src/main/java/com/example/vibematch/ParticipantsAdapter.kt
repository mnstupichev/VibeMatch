package com.example.vibematch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParticipantsAdapter(
    private val participants: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    inner class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.participantName)
        val icon: ImageView = itemView.findViewById(R.id.profileIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_person_item, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val user = participants[position]
        holder.name.text = user.username
        // Можно добавить Glide/Picasso для загрузки аватарки, если есть url
        holder.icon.setImageResource(R.drawable.account_circle_outline)
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount() = participants.size
} 