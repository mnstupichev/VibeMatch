package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibematch.api.ApiService
import com.example.vibematch.databinding.ActivityEventDetailsBinding
import com.example.vibematch.models.Event
import com.example.vibematch.models.EventLike
import com.example.vibematch.models.EventParticipant
import com.example.vibematch.models.EventParticipantWithUser
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var apiService: ApiService
    private lateinit var participantsAdapter: ParticipantsAdapter
    private var eventId: Int = 0
    private var userId: Int = 0
    private var currentEvent: Event? = null
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем eventId из Intent
        eventId = intent.getIntExtra("event_id", 0)
        userId = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id", 0)

        // Инициализируем Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Настраиваем RecyclerView для участников
        participantsAdapter = ParticipantsAdapter { participant ->
            // Открываем чужой профиль по userId
            val intent = Intent(this, OthersProfileActivity::class.java).apply {
                putExtra("user_id", participant.user.id)
            }
            startActivity(intent)
        }
        binding.rvParticipants.apply {
            adapter = participantsAdapter
            layoutManager = LinearLayoutManager(this@EventDetailsActivity)
        }

        // Загружаем данные события
        loadEventDetails()
        loadParticipants()

        // Кнопка назад
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Кнопка лайка
        binding.btnLike.setOnClickListener {
            toggleLike(eventId)
        }
    }

    private fun loadEventDetails() {
        lifecycleScope.launch {
            try {
                // Загружаем данные события
                val eventResponse = apiService.getEvent(eventId)
                if (eventResponse.isSuccessful) {
                    currentEvent = eventResponse.body()
                    updateUI()
                }

                // Загружаем лайки события
                val likesResponse = apiService.getEventLikes(eventId)
                if (likesResponse.isSuccessful) {
                    isLiked = likesResponse.body()?.any { like -> like.userId == userId } ?: false
                    updateLikeButton()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventDetailsActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadParticipants() {
        lifecycleScope.launch {
            try {
                // Загружаем пользователей, лайкнувших событие
                val likesResponse = apiService.getEventLikesWithUsers(eventId)
                if (likesResponse.isSuccessful) {
                    val users = likesResponse.body() ?: emptyList()
                    // Преобразуем User в EventParticipantWithUser для адаптера (заполним eventId и userId)
                    val participants = users.map { user ->
                        EventParticipantWithUser(
                            eventId = eventId,
                            userId = user.id,
                            status = "liked",
                            registeredAt = Date(), // Можно заменить на null или текущее время
                            user = user
                        )
                    }
                    participantsAdapter.submitList(participants)
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventDetailsActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        currentEvent?.let { event ->
            binding.tvEventTitle.text = event.title
            binding.tvEventDescription.text = event.description
            binding.tvEventLocation.text = event.location

            // Форматируем дату и время
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val startTime = event.startTime?.let { dateFormat.format(it) } ?: "Дата не указана"
            val endTime = event.endTime?.let { dateFormat.format(it) }
            binding.tvEventDateTime.text = if (endTime != null) {
                "$startTime - $endTime"
            } else {
                startTime
            }
        }
    }

    private fun updateLikeButton() {
        binding.btnLike.apply {
            setIconResource(if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart)
            iconTint = getColorStateList(if (isLiked) R.color.heart_red else R.color.light_blue)
        }
    }

    private fun toggleLike(eventId: Int) {
        if (userId == 0) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = apiService.toggleEventLike(eventId, userId)
                if (response.isSuccessful) {
                    isLiked = !isLiked
                    updateLikeButton()
                } else {
                    Toast.makeText(this@EventDetailsActivity, "Ошибка при обновлении лайка", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventDetailsActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 