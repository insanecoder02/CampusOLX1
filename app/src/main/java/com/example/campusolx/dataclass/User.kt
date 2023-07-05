package com.example.campusolx.dataclass

import com.example.campusolx.dataclass.ProfilePicture

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
    val profilePicture: ProfilePicture,
    val semester: Int,
    val upiId: String
)