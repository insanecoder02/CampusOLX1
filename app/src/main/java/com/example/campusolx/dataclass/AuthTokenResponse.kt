package com.example.campusolx.dataclass

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
    @SerializedName("token") val token: String
)