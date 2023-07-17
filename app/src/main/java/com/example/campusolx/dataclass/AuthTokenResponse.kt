package com.example.campusolx.dataclass

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
    @SerializedName("name") val name: String,
    @SerializedName("enrollmentNo") val enrollmentNo: String,
    @SerializedName("semester") val semester: Int,
    @SerializedName("branch") val branch: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("upiId") val upiId: String,
    @SerializedName("email") val email: String,
    @SerializedName("_id") val userId: String,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("token") val token: String
)
