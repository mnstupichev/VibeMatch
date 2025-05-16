package com.example.vibematch

import com.example.vibematch.models.EventParticipantWithUser
import com.example.vibematch.models.User
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class ParticipantsAdapterTest {
    @Test
    fun testAdapterItemCount() {
        val user = User(
            id = 1,
            username = "testuser",
            email = "test@example.com"
        )
        val participants = listOf(
            EventParticipantWithUser(1, 1, "interested", Date(), user),
            EventParticipantWithUser(1, 2, "interested", Date(), user)
        )
        val adapter = ParticipantsAdapter {}
        adapter.submitList(participants)
        assertEquals(2, adapter.itemCount)
    }
} 