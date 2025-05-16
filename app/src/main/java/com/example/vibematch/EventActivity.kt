package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibematch.api.ApiService
import com.example.vibematch.databinding.ActivityEventBinding
import com.example.vibematch.models.EventParticipant
import com.example.vibematch.models.EventParticipantWithUser
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding
    private lateinit var apiService: ApiService
    private lateinit var participantsAdapter: ParticipantsAdapter
    private var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем eventId из Intent
        eventId = intent.getIntExtra("event_id", 0)
        if (eventId == 0) {
            Toast.makeText(this, "Ошибка: ID события не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Инициализируем Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Настраиваем RecyclerView для участников
        participantsAdapter = ParticipantsAdapter { participant ->
            val intent = Intent(this, UserProfileActivity::class.java).apply {
                putExtra("user_id", participant.user.id)
            }
            startActivity(intent)
        }
        binding.rvParticipants.apply {
            adapter = participantsAdapter
            layoutManager = LinearLayoutManager(this@EventActivity)
        }

        // Загружаем участников
        loadParticipants()

        // Кнопка назад
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadParticipants() {
        lifecycleScope.launch {
            try {
                val response = apiService.getEventParticipantsWithUsers(eventId)
                if (response.isSuccessful) {
                    participantsAdapter.submitList(response.body() ?: emptyList())
                } else {
                    Toast.makeText(this@EventActivity, "Ошибка при загрузке участников", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 