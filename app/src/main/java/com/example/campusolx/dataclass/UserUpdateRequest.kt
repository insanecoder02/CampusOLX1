package com.example.campusolx.dataclass

data class UserUpdateRequest(
    val name: String?,
    val semester: Int?,
    val upiId: String?,
    val branch: String?,
    val contact: String?,
    val profilePicture: String?
)