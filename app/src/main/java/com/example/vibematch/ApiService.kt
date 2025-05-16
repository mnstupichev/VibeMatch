package com.example.vibematch

import com.example.vibematch.models.Event
import com.example.vibematch.models.EventParticipant
import com.example.vibematch.models.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("events/")
    suspend fun getEvents(): Response<List<Event>>

    @GET("events/{event_id}/")
    suspend fun getEvent(@Path("event_id") eventId: Int): Response<Event>

    @GET("events/{event_id}/participants/")
    suspend fun getEventParticipants(@Path("event_id") eventId: Int): Response<List<EventParticipant>>

    @GET("users/{user_id}/")
    suspend fun getUser(@Path("user_id") userId: Int): Response<User>

    @GET("users/{user_id}/events/")
    suspend fun getUserEvents(@Path("user_id") userId: Int): Response<List<EventParticipant>>

    @POST("events/{event_id}/join/")
    suspend fun joinEvent(
        @Path("event_id") eventId: Int,
        @Query("user_id") userId: Int,
        @Query("status") status: String = "interested"
    ): Response<EventParticipant>
} 