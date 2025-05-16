package com.example.vibematch.models

import java.util.Date

// Обычный участник
data class EventParticipant(
    val eventId: Int,
    val userId: Int,
    val status: String,
    val registeredAt: Date
)

// Участник с вложенным профилем пользователя
data class EventParticipantWithUser(
    val eventId: Int,
    val userId: Int,
    val status: String,
    val registeredAt: Date,
    val user: User
) 