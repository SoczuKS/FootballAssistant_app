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
    @Named("tokenRefreshClient") private val client: OkHttpClient, private val gson: Gson
) : TokenRefreshGateway {
    override fun refresh(refreshToken: String): TokenRefreshStatus = try {
        val payload =
            gson.toJson(RefreshTokenRequest(data = RefreshTokenRequest.Data(refreshToken)))
        val request = Request.Builder().url("${NetworkConfig.BASE_URL}auth/refresh").header(
            NetworkConfig.AGENT_HEADER_NAME, NetworkConfig.AGENT_HEADER_VALUE
        ).post(payload.toRequestBody("application/json".toMediaType())).build()

        client.newCall(request).execute().use { jsonResponse ->
            when {
                jsonResponse.isSuccessful -> {
                    val body = jsonResponse.body.string()
                    val response = gson.fromJson(body, RefreshTokenResponse::class.java)
                    if (response.data.accessToken.isBlank() || response.data.refreshToken.isBlank()) {
                        TokenRefreshStatus.Unavailable
                    } else {
                        TokenRefreshStatus.Success(
                            response.data.accessToken, response.data.refreshToken
                        )
                    }
                }

                jsonResponse.code == 401 -> TokenRefreshStatus.Rejected
                else -> TokenRefreshStatus.Unavailable
            }
        }
    } catch (_: Exception) {
        TokenRefreshStatus.Unavailable
    }
}
