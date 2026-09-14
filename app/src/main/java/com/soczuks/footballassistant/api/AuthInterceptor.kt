package com.soczuks.footballassistant.api

import com.soczuks.footballassistant.config.NetworkConfig
import com.soczuks.footballassistant.data.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionManager.getAccessToken() }
        val request = chain.request().newBuilder().apply {
            header(NetworkConfig.AGENT_HEADER_NAME, NetworkConfig.AGENT_HEADER_VALUE)
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()
        return chain.proceed(request)
    }
}
