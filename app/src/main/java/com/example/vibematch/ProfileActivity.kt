package com.example.vibematch

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Инициализация элементов
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)

        // Установка значений (пока заглушки)
        tvUsername.text = "USERNAME" // Здесь будет имя пользователя
        tvInfo.text = "INFO" // Здесь будет дополнительная информация
    }
}