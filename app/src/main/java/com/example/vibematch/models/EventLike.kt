package com.example.vibematch.models

import com.google.gson.annotations.SerializedName

data class EventLike(
    @SerializedName("event_id")
    val eventId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String
) 