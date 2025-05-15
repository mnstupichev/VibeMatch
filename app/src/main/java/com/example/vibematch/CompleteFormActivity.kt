package com.example.vibematch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.models.User
import com.example.vibematch.models.UserUpdate
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        val etCity = findViewById<TextInputEditText>(R.id.etCity)
        val etSpeciality = findViewById<TextInputEditText>(R.id.etSpeciality)
        val etTelegram = findViewById<TextInputEditText>(R.id.etTelegram)
        val etBio = findViewById<TextInputEditText>(R.id.etBio)
        val etGender = findViewById<TextInputEditText>(R.id.actvGender)

        // Заполняем поля текущими данными
        etCity.setText(intent.getStringExtra("city"))
        etSpeciality.setText(intent.getStringExtra("speciality"))
        etTelegram.setText(intent.getStringExtra("telegram"))
        etBio.setText(intent.getStringExtra("bio"))
        etGender.setText(intent.getStringExtra("gender"))

        // Кнопка сохранения
        findViewById<FloatingActionButton>(R.id.fabSave).setOnClickListener {
            val userUpdate = UserUpdate(
                city = etCity.text?.toString()?.takeIf { it.isNotBlank() },
                speciality = etSpeciality.text?.toString()?.takeIf { it.isNotBlank() },
                telegramLink = etTelegram.text?.toString()?.takeIf { it.isNotBlank() },
                bio = etBio.text?.toString()?.takeIf { it.isNotBlank() },
                gender = etGender.text?.toString()?.takeIf { it.isNotBlank() }
            )
            saveUserData(userUpdate)
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