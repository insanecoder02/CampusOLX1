package com.example.campusolx.dataclass

data class UserUpdateRequest(
    val name: String?,
    val enrollmentNo: String?,
    val semester: Int?,
    val upiId: String?,
    val branch: String?,
    val contact: String?,
    val email: String?,
    val profilePicture: String?
)