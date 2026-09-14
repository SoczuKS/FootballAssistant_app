package com.soczuks.footballassistant.api

fun interface TokenRefreshGateway {
    fun refresh(refreshToken: String): TokenRefreshResult
}
