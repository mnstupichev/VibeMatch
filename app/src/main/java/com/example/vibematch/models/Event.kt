package com.example.vibematch.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Event(
    @SerializedName("event_id")
    val eventId: Int,
    val title: String,
    val description: String?,
    val location: String,
    @SerializedName("start_time")
    val startTime: Date,
    @SerializedName("end_time")
    val endTime: Date?,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("is_active")
    val isActive: Boolean
) 