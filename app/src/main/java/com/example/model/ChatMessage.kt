package com.example.model

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val senderName: String,
    val initials: String,
    val timestamp: String
)