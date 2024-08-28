package com.example.simple_blog.domain.member.exception

class PasswordInvalidException(
    message: String,
): RuntimeException(message) {
}