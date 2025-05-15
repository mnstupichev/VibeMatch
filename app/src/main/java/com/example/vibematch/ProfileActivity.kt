package com.example.vibematch

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.models.User
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Инициализация API сервиса
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Инициализация элементов
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvSpeciality = findViewById<TextView>(R.id.tvSpeciality)
        val tvTelegram = findViewById<TextView>(R.id.tvTelegram)
        val tvBio = findViewById<TextView>(R.id.tvBio)

        // Кнопка назад
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack).setOnClickListener {
            startActivity(android.content.Intent(this, MainScreenActivity::class.java))
            finish()
        }

        // Кнопка изменения информации
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            currentUser?.let { user ->
                val intent = android.content.Intent(this, CompleteFormActivity::class.java).apply {
                    putExtra("user_id", user.id)
                    putExtra("username", user.username)
                    putExtra("city", user.city)
                    putExtra("speciality", user.speciality)
                    putExtra("telegram", user.telegramLink)
                    putExtra("bio", user.bio)
                    putExtra("gender", user.gender)
                }
                startActivity(intent)
            }
        }

        // Загрузка данных пользователя
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем данные при возвращении на экран
        loadUserData()
    }

    private fun loadUserData() {
        // Получаем ID пользователя из SharedPreferences
        val userId = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id", 0)
        if (userId == 0) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response: Response<User> = apiService.getUser(userId)
                if (response.isSuccessful) {
                    currentUser = response.body()
                    updateUI(currentUser)
                } else {
                    Toast.makeText(this@ProfileActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: User?) {
        user?.let {
            findViewById<TextView>(R.id.tvUsername).text = it.username
            findViewById<TextView>(R.id.tvCity).text = it.city ?: "Не указан"
            findViewById<TextView>(R.id.tvSpeciality).text = it.speciality ?: "Не указана"
            findViewById<TextView>(R.id.tvTelegram).text = it.telegramLink ?: "Не указан"
            findViewById<TextView>(R.id.tvBio).text = it.bio ?: "Нет описания"
        }
    }
}