package com.zybooks.lockedin.model

data class SessionEntry(
    val date: String,
    val duration: Long,
    val completedSessions: Int
)
