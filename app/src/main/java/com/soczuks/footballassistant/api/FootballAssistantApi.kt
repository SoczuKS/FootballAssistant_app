package com.soczuks.footballassistant.api

import com.soczuks.footballassistant.api.model.request.LoginRequest
import com.soczuks.footballassistant.api.model.request.RegisterRequest
import com.soczuks.footballassistant.api.model.response.LoginResponse
import com.soczuks.footballassistant.api.model.response.MessageResponse
import com.soczuks.footballassistant.api.model.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FootballAssistantApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserProfileResponse>
}
