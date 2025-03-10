package com.example.model

data class ChatMessage(
    val senderId: String,
    val senderName: String,
    val senderImage: String?,
    val message: String
)
