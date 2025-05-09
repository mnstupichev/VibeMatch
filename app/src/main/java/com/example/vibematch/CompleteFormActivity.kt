package com.example.vibematch
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.floatingactionbutton.FloatingActionButton


class CompleteFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_form)

        // Находим кнопку сохранения
        val fabSave = findViewById<FloatingActionButton>(R.id.fabSave)

        // Вешаем обработчик нажатия
        fabSave.setOnClickListener {
            // Создаем Intent для перехода на ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)

            // Запускаем ProfileActivity
            startActivity(intent)

            // Закрываем текущую активити (необязательно)
            finish()
        }
    }
}