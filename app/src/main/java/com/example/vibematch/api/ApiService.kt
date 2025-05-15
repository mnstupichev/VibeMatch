package com.example.vibematch.api

import com.example.vibematch.models.Event
import com.example.vibematch.models.EventLike
import com.example.vibematch.models.EventParticipant
import com.example.vibematch.models.User
import com.example.vibematch.models.UserCreate
import com.example.vibematch.models.UserUpdate
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users/")
    suspend fun createUser(@Body userCreate: UserCreate): Response<User>

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

    @PUT("users/{user_id}/")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body userUpdate: UserUpdate
    ): Response<User>

    @GET("events/{event_id}/likes/")
    suspend fun getEventLikes(@Path("event_id") eventId: Int): Response<List<EventLike>>

    @POST("events/{event_id}/like/")
    suspend fun toggleEventLike(
        @Path("event_id") eventId: Int,
        @Query("user_id") userId: Int
    ): Response<EventLike?>

    @GET("events/{event_id}/likes/check/")
    suspend fun checkEventLike(
        @Path("event_id") eventId: Int,
        @Query("user_id") userId: Int
    ): Response<Boolean>

    @GET("users/{user_id}/liked-events/")
    suspend fun getUserLikedEvents(@Path("user_id") userId: Int): Response<List<Event>>
} 