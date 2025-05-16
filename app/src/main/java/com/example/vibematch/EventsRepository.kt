package com.example.vibematch

class EventsRepository(private val api: ApiService) {
    suspend fun getEvents() = api.getEvents()
    suspend fun getEvent(eventId: Int) = api.getEvent(eventId)
    suspend fun getEventParticipants(eventId: Int) = api.getEventParticipants(eventId)
    suspend fun joinEvent(eventId: Int, userId: Int, status: String = "interested") = api.joinEvent(eventId, userId, status)
    suspend fun getUser(userId: Int) = api.getUser(userId)
    suspend fun getUserEvents(userId: Int) = api.getUserEvents(userId)
} 