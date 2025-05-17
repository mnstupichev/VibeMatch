package com.example.vibematch

import com.example.vibematch.models.User
import org.junit.Assert.*
import org.junit.Test

class UserModelTest {
    @Test
    fun testUserModelFields() {
        val user = User(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            city = "TestCity",
            speciality = "Developer",
            telegramLink = "@testuser",
            bio = "Test bio",
            gender = "Мужской",
            age = 25
        )
        assertEquals("testuser", user.username)
        assertEquals("TestCity", user.city)
        assertEquals("@testuser", user.telegramLink)
        assertEquals(25, user.age)
        assertTrue(user.isActive)
    }
} 