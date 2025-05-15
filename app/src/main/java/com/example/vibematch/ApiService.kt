package com.example.vibematch

import retrofit2.http.*

interface ApiService {
    @GET("events/")
    suspend fun getEvents(): List<Event>

    @GET("events/{event_id}/")
    suspend fun getEvent(@Path("event_id") eventId: Int): Event

    @GET("events/{event_id}/participants/")
    suspend fun getEventParticipants(@Path("event_id") eventId: Int): List<EventParticipant>

    @GET("users/{user_id}/")
    suspend fun getUser(@Path("user_id") userId: Int): User

    @GET("users/{user_id}/events/")
    suspend fun getUserEvents(@Path("user_id") userId: Int): List<EventParticipant>

    @POST("events/{event_id}/join/")
    suspend fun joinEvent(
        @Path("event_id") eventId: Int,
        @Query("user_id") userId: Int,
        @Query("status") status: String = "interested"
    ): EventParticipant
} 