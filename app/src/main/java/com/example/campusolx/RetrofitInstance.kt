package com.example.campusolx

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object for creating and configuring Retrofit instances
object RetrofitInstance {
    // Define the base URL for your API
    // Use either the commented out URL for a local development server or the remote server URL
//    private const val BASE_URL = "http://10.0.2.2:3000/"
    private const val BASE_URL = "https://puzzled-fox-shrug.cyclic.app"

    // Function to create a Retrofit instance
    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            // Set the base URL for the Retrofit instance
            .baseUrl(BASE_URL)
            // Add GsonConverterFactory to handle JSON serialization and deserialization
            .addConverterFactory(GsonConverterFactory.create())
            // Build and return the Retrofit instance
            .build()
    }
}
