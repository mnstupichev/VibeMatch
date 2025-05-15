package com.example.vibematch.models

import java.util.Date

data class EventParticipant(
    val eventId: Int,
    val userId: Int,
    val status: String,
    val registeredAt: Date
) 