package com.example.campusolx

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)