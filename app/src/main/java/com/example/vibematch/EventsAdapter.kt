package com.example.vibematch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibematch.models.Event
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(
    private val events: List<Event>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.activityName)
        val date: TextView = itemView.findViewById(R.id.activityDate)
        val time: TextView = itemView.findViewById(R.id.activityTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.name.text = event.title
        
        // Format date and time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(event.startTime)
        holder.date.text = formattedDate.substring(0, 10) // YYYY-MM-DD
        holder.time.text = formattedDate.substring(11, 16) // HH:MM
        
        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    override fun getItemCount() = events.size
}