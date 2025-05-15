package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainScreenActivity : AppCompatActivity() {
    private val repository = EventsRepository(ApiClient.apiService)
    private var events: List<Event> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // Обработчик для кнопки профиля
        findViewById<androidx.cardview.widget.CardView>(R.id.profileCard).setOnClickListener {
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }

        val btnLeft = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack)
        val btnRight = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnForward)
        val tvIndicator = findViewById<TextView>(R.id.tvIndicator)

        btnLeft.setOnClickListener {
            if (events.isNotEmpty()) {
                currentIndex = (currentIndex - 1 + events.size) % events.size
                showEvent()
            }
        }
        btnRight.setOnClickListener {
            if (events.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % events.size
                showEvent()
            }
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.eventCard).setOnClickListener {
            if (events.isNotEmpty()) {
                val event = events[currentIndex]
                val intent = Intent(this, EventActivity::class.java)
                intent.putExtra("event_id", event.event_id)
                startActivity(intent)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val loadedEvents = repository.getEvents()
            withContext(Dispatchers.Main) {
                events = loadedEvents
                currentIndex = 0
                showEvent()
            }
        }
    }

    private fun showEvent() {
        if (events.isEmpty()) return
        val event = events[currentIndex]
        findViewById<TextView>(R.id.tvEventTitle).text = event.title
        findViewById<TextView>(R.id.tvEventDate).text = event.start_time.substring(0, 10)
        findViewById<TextView>(R.id.tvEventLocation).text = event.location
        findViewById<TextView>(R.id.tvEventDescription).text = event.description ?: ""
        findViewById<TextView>(R.id.tvIndicator).text = "${currentIndex + 1}/${events.size}"
    }
}