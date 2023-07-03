package com.example.campusolx

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
    @SerializedName("token") val token: String
)