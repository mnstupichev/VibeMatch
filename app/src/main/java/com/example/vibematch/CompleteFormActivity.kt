package com.example.vibematch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.models.User
import com.example.vibematch.models.UserUpdate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import retrofit2.Response

class CompleteFormActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_form)

        // Инициализация API сервиса
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Получаем ID пользователя из Intent
        userId = intent.getIntExtra("user_id", 0)
        if (userId == 0) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Инициализация полей ввода
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etAge = findViewById<TextInputEditText>(R.id.etAge)
        val etGender = findViewById<TextInputEditText>(R.id.etGender)
        val etBio = findViewById<TextInputEditText>(R.id.etBio)
        val etTelegram = findViewById<TextInputEditText>(R.id.etTelegram)
        val etCity = findViewById<TextInputEditText>(R.id.etCity)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        // Загружаем текущие данные пользователя
        loadUserProfile(etUsername, etAge, etGender, etBio, etTelegram, etCity)

        // Кнопка сохранения
        btnSave.setOnClickListener {
            val username = etUsername.text?.toString()?.takeIf { it.isNotBlank() }
            val age = etAge.text?.toString()?.toIntOrNull()
            val gender = etGender.text?.toString()?.takeIf { it.isNotBlank() }
            val bio = etBio.text?.toString()?.takeIf { it.isNotBlank() }
            val telegram = etTelegram.text?.toString()?.takeIf { it.isNotBlank() }
            val city = etCity.text?.toString()?.takeIf { it.isNotBlank() }
            val userUpdate = UserUpdate(
                username = username,
                age = age,
                gender = gender,
                bio = bio,
                telegramLink = telegram,
                city = city
            )
            saveUserData(userUpdate)
        }
    }

    private fun loadUserProfile(
        etUsername: TextInputEditText,
        etAge: TextInputEditText,
        etGender: TextInputEditText,
        etBio: TextInputEditText,
        etTelegram: TextInputEditText,
        etCity: TextInputEditText
    ) {
        lifecycleScope.launch {
            try {
                val response = apiService.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Заполняем поля данными пользователя
                        etUsername.setText(user.username)
                        etAge.setText(user.age?.toString() ?: "")
                        etGender.setText(user.gender ?: "")
                        etBio.setText(user.bio ?: "")
                        etTelegram.setText(user.telegramLink ?: "")
                        etCity.setText(user.city ?: "")
                    } else {
                        Toast.makeText(this@CompleteFormActivity, "Ошибка: данные пользователя не найдены", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Пользователь не найден"
                        401 -> "Требуется авторизация"
                        else -> "Ошибка при загрузке профиля: ${response.errorBody()?.string() ?: "Неизвестная ошибка"}"
                    }
                    Toast.makeText(this@CompleteFormActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CompleteFormActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveUserData(userUpdate: UserUpdate) {
        lifecycleScope.launch {
            try {
                val response: Response<User> = apiService.updateUser(userId, userUpdate)
                if (response.isSuccessful) {
                    Toast.makeText(this@CompleteFormActivity, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
                    // Возвращаемся на главный экран
                    startActivity(android.content.Intent(this@CompleteFormActivity, MainScreenActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@CompleteFormActivity, 
                        "Ошибка сохранения данных: ${errorBody ?: response.code()}", 
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CompleteFormActivity, 
                    "Ошибка: ${e.message}", 
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}