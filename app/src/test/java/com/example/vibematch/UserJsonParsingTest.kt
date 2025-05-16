package com.example.vibematch

import com.example.vibematch.models.User
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class UserJsonParsingTest {
    @Test
    fun testUserParsing() {
        val json = """
            {
                "user_id": 1,
                "username": "testuser",
                "email": "test@example.com",
                "city": "TestCity",
                "speciality": "Developer",
                "telegram_link": "@testuser",
                "bio": "Test bio",
                "gender": "Мужской",
                "age": 25,
                "is_active": true
            }
        """.trimIndent()
        val user = Gson().fromJson(json, User::class.java)
        assertEquals("testuser", user.username)
        assertEquals("TestCity", user.city)
        assertEquals("@testuser", user.telegramLink)
        assertEquals(25, user.age)
        assertTrue(user.isActive)
    }
} 