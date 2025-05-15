package com.example.vibematch

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Инициализация элементов
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)

        // Кнопка назад
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack).setOnClickListener {
            startActivity(android.content.Intent(this, MainScreenActivity::class.java))
            finish()
        }

        // Кнопка изменения информации
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            startActivity(android.content.Intent(this, CompleteFormActivity::class.java))
        }

        // Установка значений (пока заглушки)
        tvUsername.text = "USERNAME" // Здесь будет имя пользователя
        tvInfo.text = "INFO" // Здесь будет дополнительная информация
    }
}