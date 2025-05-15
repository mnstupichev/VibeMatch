package com.example.vibematch
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.models.User
import com.example.vibematch.models.UserCreate
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Проверяем, заполнен ли профиль пользователя
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", 0)
        
        if (userId != 0) {
            // Проверяем, заполнен ли профиль
            lifecycleScope.launch {
                try {
                    val response: Response<User> = apiService.getUser(userId)
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user?.city == null || user.speciality == null || user.telegramLink == null) {
                            // Профиль не заполнен полностью
                            startActivity(Intent(this@MainActivity, CompleteFormActivity::class.java).apply {
                                putExtra("user_id", userId)
                            })
                        } else {
                            // Профиль заполнен, переходим на главный экран
                            startActivity(Intent(this@MainActivity, MainScreenActivity::class.java))
                        }
                        finish()
                        return@launch
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                // В случае ошибки показываем экран входа
                setContentView(R.layout.activity_main)
                setupLoginUI()
            }
        } else {
            // Пользователь не залогинен, показываем экран входа
            setContentView(R.layout.activity_main)
            setupLoginUI()
        }
    }

    private fun setupLoginUI() {
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text?.toString()?.trim()
            val email = etEmail.text?.toString()?.trim()
            val password = etPassword.text?.toString()?.trim()

            when {
                username.isNullOrEmpty() -> etUsername.error = "Please enter username"
                email.isNullOrEmpty() -> etEmail.error = "Please enter email"
                password.isNullOrEmpty() -> etPassword.error = "Please enter password"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    etEmail.error = "Invalid email format"
                else -> {
                    // Создаем нового пользователя
                    lifecycleScope.launch {
                        try {
                            val userCreate = UserCreate(
                                username = username,
                                email = email,
                                password = password
                            )
                            val response: Response<User> = apiService.createUser(userCreate)
                            
                            if (response.isSuccessful) {
                                val user = response.body()
                                if (user != null) {
                                    // Сохраняем ID пользователя
                                    getSharedPreferences("UserProfile", MODE_PRIVATE).edit()
                                        .putInt("user_id", user.id)
                                        .putString("username", user.username)
                                        .putString("email", user.email)
                                        .apply()

                                    // Переходим на экран заполнения профиля
                                    val intent = Intent(this@MainActivity, CompleteFormActivity::class.java)
                                    intent.putExtra("user_id", user.id)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@MainActivity, 
                                    "Ошибка регистрации: ${errorBody ?: response.code()}", 
                                    Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, 
                                "Ошибка: ${e.message}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}