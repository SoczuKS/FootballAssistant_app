package com.soczuks.footballassistant.api.model

data class AppRelease(
    val versionCode: Int,
    val versionName: String,
    val minimumSupportedVersionCode: Int,
    val downloadUrl: String,
    val sha256: String,
    val sizeBytes: Long,
    val publishedAt: String,
    val releaseNotes: List<String>
)
