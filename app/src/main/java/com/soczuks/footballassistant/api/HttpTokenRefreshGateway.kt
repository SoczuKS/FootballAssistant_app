package com.soczuks.footballassistant.api

import com.google.gson.Gson
import com.soczuks.footballassistant.api.model.request.RefreshTokenRequest
import com.soczuks.footballassistant.api.model.response.RefreshTokenResponse
import com.soczuks.footballassistant.config.NetworkConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class HttpTokenRefreshGateway @Inject constructor(
    @Named("tokenRefreshClient") private val client: OkHttpClient,
    private val gson: Gson
) : TokenRefreshGateway {
    override fun refresh(refreshToken: String): TokenRefreshResult = try {
        val payload = gson.toJson(RefreshTokenRequest(refreshToken))
        val request = Request.Builder().url("${NetworkConfig.BASE_URL}auth/refresh").header(
            NetworkConfig.AGENT_HEADER_NAME, NetworkConfig.AGENT_HEADER_VALUE
        ).post(payload.toRequestBody("application/json".toMediaType())).build()

        client.newCall(request).execute().use { response ->
            when {
                response.isSuccessful -> {
                    val body = response.body.string()
                    val tokens = gson.fromJson(body, RefreshTokenResponse::class.java)
                    if (tokens.accessToken.isBlank() || tokens.refreshToken.isBlank()) {
                        TokenRefreshResult.Unavailable
                    } else {
                        TokenRefreshResult.Success(tokens.accessToken, tokens.refreshToken)
                    }
                }

                response.code == 401 -> TokenRefreshResult.Rejected
                else -> TokenRefreshResult.Unavailable
            }
        }
    } catch (_: Exception) {
        TokenRefreshResult.Unavailable
    }
}
