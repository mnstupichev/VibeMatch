package com.example.vibematch

data class Event(
    val event_id: Int,
    val title: String,
    val description: String?,
    val location: String,
    val start_time: String,
    val end_time: String?,
    val created_at: String?,
    val is_active: Boolean
)