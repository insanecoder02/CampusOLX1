package com.example.campusolx.dataclass

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)