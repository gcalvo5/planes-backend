package com.planes.dtos

data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val username: String
)
