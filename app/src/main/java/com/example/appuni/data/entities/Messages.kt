package com.example.appuni.data.entities

data class Messages(
    val id: Long? = null,
    val senderId: Long,
    val receiverId: Long,
    val messageText: String,
    val timestamp: String
)