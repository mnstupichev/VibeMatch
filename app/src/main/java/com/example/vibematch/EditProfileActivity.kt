package com.example.vibematch

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.databinding.ActivityEditProfileBinding
import com.example.vibematch.models.UserUpdate
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var apiService: ApiService
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
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

        // Инициализация спиннера для выбора пола
        val genders = arrayOf("Мужской", "Женский", "Другой")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        // Настройка кнопки назад
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Настройка кнопки сохранения
        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        // Загрузка текущих данных профиля
        loadProfile()
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                // Показываем индикатор загрузки
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false

                val response = apiService.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Заполняем поля данными пользователя
                        binding.etUsername.setText(user.username)
                        binding.etBio.setText(user.bio ?: "")
                        binding.etAge.setText(user.age?.toString() ?: "")
                        binding.etInterests.setText(user.interests ?: "")
                        
                        // Устанавливаем пол в спиннере
                        val genderPosition = (binding.spinnerGender.adapter as ArrayAdapter<String>)
                            .getPosition(user.gender ?: "Другой")
                        if (genderPosition >= 0) {
                            binding.spinnerGender.setSelection(genderPosition)
                        }
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Ошибка: данные пользователя не найдены", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Пользователь не найден"
                        401 -> "Требуется авторизация"
                        else -> "Ошибка при загрузке профиля: ${response.errorBody()?.string() ?: "Неизвестная ошибка"}"
                    }
                    Toast.makeText(this@EditProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Error loading profile", e)
                Toast.makeText(this@EditProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            } finally {
                // Скрываем индикатор загрузки
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
            }
        }
    }

    private fun saveProfile() {
        val username = binding.etUsername.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val ageStr = binding.etAge.text.toString().trim()
        val age = if (ageStr.isNotEmpty()) ageStr.toIntOrNull() else null
        val gender = binding.spinnerGender.selectedItem.toString()
        val interests = binding.etInterests.text.toString().trim()

        // Валидация данных
        if (username.isBlank()) {
            binding.etUsername.error = "Имя пользователя не может быть пустым"
            return
        }
        if (username.length < 3) {
            binding.etUsername.error = "Имя пользователя должно содержать минимум 3 символа"
            return
        }
        if (username.length > 50) {
            binding.etUsername.error = "Имя пользователя не должно превышать 50 символов"
            return
        }
        if (bio.length > 500) {
            binding.etBio.error = "Биография не должна превышать 500 символов"
            return
        }
        if (age != null && (age < 18 || age > 100)) {
            binding.etAge.error = "Возраст должен быть от 18 до 100 лет"
            return
        }

        // Блокируем кнопку и показываем индикатор загрузки
        binding.btnSave.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val userUpdate = UserUpdate(
                    username = username,
                    bio = bio,
                    age = age,
                    gender = gender,
                    interests = interests
                )

                val response = apiService.updateUser(userId, userUpdate)
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    if (updatedUser != null) {
                        // Обновляем данные в SharedPreferences
                        getSharedPreferences("UserProfile", MODE_PRIVATE).edit().apply {
                            putString("username", updatedUser.username)
                            putString("bio", updatedUser.bio)
                            putInt("age", updatedUser.age ?: 0)
                            putString("gender", updatedUser.gender)
                            putString("interests", updatedUser.interests)
                            apply()
                        }

                        Toast.makeText(this@EditProfileActivity, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Ошибка: пустой ответ от сервера", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditProfileActivity", "Error response: $errorBody")
                    val errorMessage = when (response.code()) {
                        404 -> "Пользователь не найден"
                        400 -> {
                            // Пытаемся распарсить ошибки валидации
                            try {
                                val errorJson = JSONObject(errorBody ?: "{}")
                                val errors = mutableListOf<String>()
                                errorJson.keys().forEach { key ->
                                    val value = errorJson.get(key)
                                    if (value is JSONArray) {
                                        for (i in 0 until value.length()) {
                                            errors.add("$key: ${value.getString(i)}")
                                        }
                                    } else {
                                        errors.add("$key: $value")
                                    }
                                }
                                if (errors.isNotEmpty()) {
                                    errors.joinToString("\n")
                                } else {
                                    "Неверные данные профиля"
                                }
                            } catch (e: Exception) {
                                "Неверные данные профиля"
                            }
                        }
                        401 -> "Требуется авторизация"
                        else -> "Ошибка при обновлении профиля: ${errorBody ?: "Неизвестная ошибка"}"
                    }
                    Toast.makeText(this@EditProfileActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Exception during profile update", e)
                Toast.makeText(this@EditProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Разблокируем кнопку и скрываем индикатор загрузки
                binding.btnSave.isEnabled = true
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем данные профиля при возвращении на экран
        loadProfile()
    }
} 