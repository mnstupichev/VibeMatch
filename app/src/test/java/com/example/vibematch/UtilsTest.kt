package com.example.vibematch

import org.junit.Assert.*
import org.junit.Test

object Utils {
    fun formatAge(age: Int?): String = age?.toString() ?: "Не указан"
}

class UtilsTest {
    @Test
    fun testFormatAge() {
        assertEquals("25", Utils.formatAge(25))
        assertEquals("Не указан", Utils.formatAge(null))
    }
} 