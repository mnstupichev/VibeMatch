package com.example.vibematch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventsAdapter(
    private val activities: MutableList<Event>, // Используем MutableList для изменений
    private val onItemClick: (Event) -> Unit,
    private val onLikeClick: (Event) -> Unit // Новый callback для лайков
) : RecyclerView.Adapter<EventsAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.activityImage)
        val name: TextView = itemView.findViewById(R.id.activityName)
        val date: TextView = itemView.findViewById(R.id.activityDate)
        val time: TextView = itemView.findViewById(R.id.activityTime)
        val btnLike: ImageButton = itemView.findViewById(R.id.btnLike) // Добавляем кнопку

        fun bind(event: Event) {
            // Обновляем иконку сердца
            updateLikeIcon(event.isLiked)

            btnLike.setOnClickListener {
                event.isLiked = !event.isLiked
                updateLikeIcon(event.isLiked)
                onLikeClick(event) // Уведомляем внешний код
            }
        }

        private fun updateLikeIcon(isLiked: Boolean) {
            val iconRes = if (isLiked) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart
            }
            btnLike.setImageResource(iconRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_event_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]

        // Здесь можно использовать Glide/Picasso для загрузки изображений
        holder.image.setImageResource(activity.imageRes)
        holder.name.text = activity.name
        holder.date.text = activity.date
        holder.time.text = activity.time

        holder.bind(activity) // Вызываем метод bind для настройки кнопки

        holder.itemView.setOnClickListener {
            onItemClick(activity)
        }
    }

    override fun getItemCount() = activities.size
}