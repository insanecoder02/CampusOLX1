package com.example.campusolx.interfaces

import retrofit2.Call
import com.example.campusolx.dataclass.AuthTokenResponse
import com.example.campusolx.dataclass.ForgotPasswordRequest
import com.example.campusolx.dataclass.LoginRequest
import com.example.campusolx.dataclass.RegisterRequest
import com.example.campusolx.dataclass.ResetPasswordRequest
import com.example.campusolx.dataclass.VerifyRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

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

    

    @POST("api/user/logout")
    fun logout(): Call<Void>
}
