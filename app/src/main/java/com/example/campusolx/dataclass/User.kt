package com.example.campusolx.dataclass

data class User(
    val __v: Int,
    val _id: String,
    val branch: String,
    val contact: String,
    val email: String,
    val enrollmentNo: String,
    val isVerified: Boolean,
    val name: String,
    val password: String,
    val profilePicture: Image,
    val semester: Int,
    val upiId: String
)