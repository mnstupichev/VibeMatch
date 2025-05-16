package com.example.vibematch

data class User(
    val user_id: Int,
    val email: String,
    val username: String,
    val gender: String?,
    val city: String?,
    val telegram_link: String?,
    val speciality: String?,
    val profile_picture_url: String?,
    val bio: String?,
    val is_active: Boolean
) 