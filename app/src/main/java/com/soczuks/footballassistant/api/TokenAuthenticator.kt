package com.soczuks.footballassistant.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(private val tokenRefresher: SessionTokenRefresher) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() } ?: return null

        val refreshedAccessToken =
            tokenRefresher.refreshAccessToken(failedAccessToken) ?: return null

        return response.request.newBuilder().header("Authorization", "Bearer $refreshedAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var previous = response.priorResponse
        while (previous != null) {
            count++
            previous = previous.priorResponse
        }
        return count
    }
}
