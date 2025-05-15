package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventActivity : AppCompatActivity() {
    private val repository = EventsRepository(ApiClient.apiService)
    private var eventId: Int = -1
    private lateinit var participantsAdapter: ParticipantsAdapter
    private val participants = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventId = intent.getIntExtra("event_id", -1)
        if (eventId == -1) finish()

        val tvEventDate = findViewById<TextView>(R.id.tvEventDate)
        val tvEventLocation = findViewById<TextView>(R.id.tvEventLocation)
        val tvEventDescription = findViewById<TextView>(R.id.tvEventDescription)
        val recyclerView = findViewById<RecyclerView>(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        participantsAdapter = ParticipantsAdapter(participants) { user ->
            val intent = Intent(this, OthersProfileActivity::class.java)
            intent.putExtra("user_id", user.user_id)
            startActivity(intent)
        }
        recyclerView.adapter = participantsAdapter

        findViewById<androidx.cardview.widget.CardView>(R.id.profileCard).setOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val event = repository.getEvent(eventId)
            val eventParticipants = repository.getEventParticipants(eventId)
            val users = eventParticipants.map { repository.getUser(it.user_id) }
            withContext(Dispatchers.Main) {
                tvEventDate.text = event.start_time.substring(0, 10)
                tvEventLocation.text = event.location
                tvEventDescription.text = event.description ?: ""
                participants.clear()
                participants.addAll(users)
                participantsAdapter.notifyDataSetChanged()
            }
        }
    }
} 