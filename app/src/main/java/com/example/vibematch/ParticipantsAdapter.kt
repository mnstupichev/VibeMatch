package com.example.vibematch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibematch.models.EventParticipantWithUser

class ParticipantsAdapter(private val onItemClick: (EventParticipantWithUser) -> Unit) : RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {
    private var participants: List<EventParticipantWithUser> = emptyList()

    fun submitList(newParticipants: List<EventParticipantWithUser>) {
        participants = newParticipants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val participant = participants[position]
        holder.bind(participant)
    }

    override fun getItemCount() = participants.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tvParticipantName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(participants[position])
                }
            }
        }

        fun bind(participant: EventParticipantWithUser) {
            textView.text = participant.user.username
        }
    }
} 