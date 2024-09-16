package com.example.simple_blog.domain.chat.dto

data class Message(
    val senderId: Long,
    val message: String,
)
