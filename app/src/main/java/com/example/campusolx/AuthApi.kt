package com.example.campusolx

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("Accept: application/json")
    @POST("api/user/login")
    fun login(@Body request: LoginRequest): Call<AuthTokenResponse>

    @FormUrlEncoded
    @POST("api/user/register")
    fun register(
        @Field("name") name: String,
        @Field("enrollmentNo") enrollmentNo: String,
        @Field("semester") semester: Int,
        @Field("upiId") upiId: String,
        @Field("branch") branch: String,
        @Field("contact") contact: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("api/user/verify")
    fun verify(
        @Field("email") code: String,
        @Field("code") email: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("api/user/forgotpassword")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("api/user/reset")
    fun resetPassword(
        @Field("email") email: String,
        @Field("token") token: String,
        @Field("newPassword") newPassword: String
    ): Call<Void>

    @POST("api/user/logout")
    fun logout(): Call<Void>
}
