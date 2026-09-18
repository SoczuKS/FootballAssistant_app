package com.soczuks.footballassistant.utility

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun sha256Hex(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    FileInputStream(file).use { stream ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = stream.read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
