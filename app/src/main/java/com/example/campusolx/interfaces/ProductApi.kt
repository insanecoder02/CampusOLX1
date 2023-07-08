package com.example.campusolx.interfaces

import com.example.campusolx.dataclass.Product
import com.example.campusolx.dataclass.ProductRequest
import com.example.campusolx.dataclass.ProductResponse
import com.example.campusolx.dataclass.UploadProductImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductApi {
    @GET("api/products")
    fun getAllProducts(@Header("Authorization") accessToken: String): Call<ProductResponse>

    @GET("api/products/user")
    fun getAllProductsOfUser(@Header("Authorization") accessToken: String): Call<ProductResponse>

    @GET("api/products/{id}")
    fun getProductById(
        @Header("Authorization") accessToken: String,
        @Path("id") productId: String
    ): Call<Product>

    @Headers("Accept: application/json")
    @POST("api/products/create")
    fun createProduct(
        @Header("Authorization") accessToken: String,
        @Body request: ProductRequest
    ): Call<Product>

    @PUT("api/products/{id}")
    fun updateProduct(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String,
        @Body request: ProductRequest
    ): Call<Product>

    @DELETE("api/products/{id}")
    fun deleteProduct(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<Void>

    @Multipart
    @POST("api/product/upload")
    fun uploadProductImage(
        @Header("Authorization") accessToken: String,
        @Part image: MultipartBody.Part
    ): Call<UploadProductImageResponse>
}