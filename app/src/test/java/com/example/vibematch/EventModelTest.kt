package com.example.vibematch

import com.example.vibematch.models.Event
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class EventModelTest {
    @Test
    fun testEventModelFields() {
        val now = Date()
        val event = Event(
            eventId = 1,
            title = "Hackathon",
            description = "Best hackathon!",
            location = "Moscow",
            startTime = now,
            endTime = null,
            createdAt = now,
            isActive = true
        )
        assertEquals(1, event.eventId)
        assertEquals("Hackathon", event.title)
        assertEquals("Moscow", event.location)
        assertTrue(event.isActive)
    }
} 