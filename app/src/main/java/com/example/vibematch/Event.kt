package com.example.vibematch

data class Event(
    val id: Int,
    val name: String,
    val date: String,
    val time: String,
    val imageRes: Int,
    var isLiked: Boolean = false // Добавляем состояние лайка
)