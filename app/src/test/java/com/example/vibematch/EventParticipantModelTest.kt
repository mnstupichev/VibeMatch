package com.example.vibematch

import com.example.vibematch.models.EventParticipant
import com.example.vibematch.models.EventParticipantWithUser
import com.example.vibematch.models.User
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class EventParticipantModelTest {
    @Test
    fun testEventParticipantFields() {
        val now = Date()
        val participant = EventParticipant(
            eventId = 1,
            userId = 2,
            status = "interested",
            registeredAt = now
        )
        assertEquals(1, participant.eventId)
        assertEquals(2, participant.userId)
        assertEquals("interested", participant.status)
        assertEquals(now, participant.registeredAt)
    }

    @Test
    fun testEventParticipantWithUser() {
        val now = Date()
        val user = User(
            id = 2,
            username = "testuser",
            email = "test@example.com"
        )
        val participantWithUser = EventParticipantWithUser(
            eventId = 1,
            userId = 2,
            status = "interested",
            registeredAt = now,
            user = user
        )
        assertEquals(1, participantWithUser.eventId)
        assertEquals(2, participantWithUser.userId)
        assertEquals("testuser", participantWithUser.user.username)
    }
} 