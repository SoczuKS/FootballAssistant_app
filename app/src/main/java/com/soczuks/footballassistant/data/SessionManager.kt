package com.soczuks.footballassistant.data

interface SessionManager {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getUserId(): Int?
    suspend fun saveUserId(userId: Int)
    suspend fun clearSession()
}
