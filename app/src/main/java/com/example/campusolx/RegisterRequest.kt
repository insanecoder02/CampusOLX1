package com.example.campusolx

data class RegisterRequest(
    val name: String,
    val enrollmentNo: String,
    val semester: Int,
    val upiId: String,
    val branch: String,
    val contact: String,
    val email: String,
    val password: String
)
