package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // Обработчик для кнопки профиля
        findViewById<androidx.cardview.widget.CardView>(R.id.profileCard).setOnClickListener {
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }

        // Создаём тестовые данные с изображениями
        val events = mutableListOf(
            Event(
                id = 1,
                name = "ITMO Family Day",
                date = "25.03.2025",
                time = "10:00",
                imageRes = R.drawable.itmo,
                isLiked = false
            ),
            Event(
                id = 2,
                name = "Панчлайн",
                date = "29.04.2025",
                time = "18:30",
                imageRes = R.drawable.itmo,
                isLiked = true
            )
        )

        // Настройка RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventsAdapter(
            activities = events,
            onItemClick = { event ->
                // Переход на экран деталей события
                val intent = Intent(this, EventDetailsActivity::class.java).apply {
                    putExtra("event_id", event.id)
                }
                startActivity(intent)
            },
            onLikeClick = { event ->
                // Обновляем состояние лайка и сохраняем (пример)
                event.isLiked = !event.isLiked
                updateEventInDatabase(event) // Ваш метод для сохранения
            }
        )
    }

    private fun updateEventInDatabase(event: Event) {
        // Здесь логика сохранения состояния лайка в БД или SharedPreferences
    }
}