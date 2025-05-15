package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.databinding.ActivityUserProfileBinding
import com.example.vibematch.models.User
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var apiService: ApiService
    private var userId: Int = 0
    private lateinit var likedEventsAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем userId из Intent
        userId = intent.getIntExtra("user_id", 0)
        if (userId == 0) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Инициализация API сервиса
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Настройка кнопки назад
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Настройка кнопки редактирования профиля
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        // Инициализация адаптера для понравившихся событий
        likedEventsAdapter = EventAdapter { event ->
            if (event.eventId > 0) {
                val intent = Intent(this, EventDetailsActivity::class.java)
                intent.putExtra("event_id", event.eventId)
                startActivity(intent)
            }
        }

        // Настройка RecyclerView
        binding.rvLikedEvents.apply {
            adapter = likedEventsAdapter
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
            
            // Добавляем разделители между элементами
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            
            // Включаем оптимизации для улучшения производительности
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }

        // Загрузка данных профиля
        loadUserProfile()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                // Загружаем данные пользователя
                val userResponse = apiService.getUser(userId)
                if (userResponse.isSuccessful) {
                    val user = userResponse.body()
                    user?.let {
                        binding.tvUsername.text = it.username
                        binding.tvEmail.text = it.email
                        binding.tvBio.text = it.bio ?: "Нет информации о себе"
                    }
                }

                // Загружаем понравившиеся события
                val likedEventsResponse = apiService.getUserLikedEvents(userId)
                if (likedEventsResponse.isSuccessful) {
                    val likedEvents = likedEventsResponse.body() ?: emptyList()
                    likedEventsAdapter.submitList(likedEvents, likedEvents.mapNotNull { it.eventId }.toSet())
                }
            } catch (e: Exception) {
                Toast.makeText(this@UserProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем данные при возвращении на экран
        loadUserProfile()
    }
} 