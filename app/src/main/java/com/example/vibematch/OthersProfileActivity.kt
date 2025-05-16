package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.models.User
import kotlinx.coroutines.launch

class OthersProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)

        val userId = intent.getIntExtra("user_id", -1)
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: user_id не передан", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvGender = findViewById<TextView>(R.id.tvGender)
        val tvTelegram = findViewById<TextView>(R.id.tvTelegram)
        val tvBio = findViewById<TextView>(R.id.tvBio)
        val tvAge = findViewById<TextView>(R.id.tvAge)

        lifecycleScope.launch {
            try {
                val response = apiService.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        tvUsername.text = user.username
                        tvCity.text = user.city ?: "Город не указан"
                        tvGender.text = user.gender ?: "Пол не указан"
                        tvTelegram.text = user.telegramLink ?: "Telegram не указан"
                        tvBio.text = user.bio ?: "Нет описания"
                        tvAge.text = user.age?.toString() ?: "Возраст не указан"
                    }
                } else {
                    Toast.makeText(this@OthersProfileActivity, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OthersProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        val eventId = intent.getIntExtra("event_id", -1)
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, EventActivity::class.java)
            if (eventId != -1) intent.putExtra("event_id", eventId)
            startActivity(intent)
            finish()
        }
    }
} 