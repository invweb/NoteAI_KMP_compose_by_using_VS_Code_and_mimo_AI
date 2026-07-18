package com.noteai

import kotlinx.datetime.LocalDateTime

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0, 0),
    val updatedAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0, 0)
)
