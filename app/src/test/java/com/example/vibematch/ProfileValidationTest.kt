package com.example.vibematch

import org.junit.Assert.*
import org.junit.Test

class ProfileValidationTest {
    fun isProfileValid(username: String?, age: String?, gender: String?, bio: String?): Boolean {
        return !username.isNullOrBlank() && !age.isNullOrBlank() && !gender.isNullOrBlank() && !bio.isNullOrBlank()
    }

    @Test
    fun testProfileValid() {
        assertTrue(isProfileValid("user", "22", "Мужской", "bio"))
    }

    @Test
    fun testProfileInvalid_emptyUsername() {
        assertFalse(isProfileValid("", "22", "Мужской", "bio"))
    }

    @Test
    fun testProfileInvalid_nullAge() {
        assertFalse(isProfileValid("user", null, "Мужской", "bio"))
    }
} 