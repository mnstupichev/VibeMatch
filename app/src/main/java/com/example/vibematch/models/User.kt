package com.example.vibematch.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val id: Int,
    val username: String,
    val email: String,
    val city: String? = null,
    val speciality: String? = null,
    @SerializedName("telegram_link")
    val telegramLink: String? = null,
    val bio: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val interests: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true
)

data class UserUpdate(
    val username: String? = null,
    val gender: String? = null,
    val city: String? = null,
    @SerializedName("telegram_link") val telegramLink: String? = null,
    val speciality: String? = null,
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    val bio: String? = null,
    val age: Int? = null,
    val interests: String? = null
) 