package com.example.campusolx.interfaces

import retrofit2.Call
import com.example.campusolx.dataclass.AuthTokenResponse
import com.example.campusolx.dataclass.ForgotPasswordRequest
import com.example.campusolx.dataclass.LoginRequest
import com.example.campusolx.dataclass.RegisterRequest
import com.example.campusolx.dataclass.ResetPasswordRequest
import com.example.campusolx.dataclass.User
import com.example.campusolx.dataclass.UserRequest
import com.example.campusolx.dataclass.VerifyRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
    @Headers("Accept: application/json")
    @POST("api/user/login")
    fun login(@Body request: LoginRequest): Call<AuthTokenResponse>

    @Headers("Accept: application/json")
    @POST("api/user")
    fun register(@Body request: RegisterRequest): Call<Void>

    @Headers("Accept: application/json")
    @POST("api/user/verify")
    fun verify(@Body request: VerifyRequest): Call<Void>

    @Headers("Accept: application/json")
    @POST("api/user/forgotpassword")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<Void>

    @Headers("Accept: application/json")
    @POST("api/user/reset")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<Void>

    @PUT("api/users/{id}")
    fun updateUser(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body request: UserRequest
    ): Call<User>

    @DELETE("api/users/{id}")
    fun deleteUser(
        @Header("Authorization") accessToken: String,
        @Path("id") userId: String
    ): Call<Void>

    @POST("api/user/logout")
    fun logout(): Call<Void>
}
