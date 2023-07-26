package com.example.campusolx.activites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdDetailsBinding
import com.example.campusolx.dataclass.Product
import com.example.campusolx.dataclass.User
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.interfaces.AuthApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdDetailsBinding
    private lateinit var productApi: ProductApi
    private lateinit var accessToken: String
    private lateinit var authApi: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("product_id") ?: ""

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)
        authApi = retrofit.create(AuthApi::class.java)

        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        val call = productApi.getProductById(accessToken, productId)

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        // Fetch the user details using createdBy (user ID)
                        fetchUserDetails(product.createdBy)
                        displayProductData(product)
                    } else {
                        // Handle case where product is null
                    }
                } else {
                    // Handle unsuccessful API response
                    showToast("Failed to fetch product details")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                // Handle API call failure
                showToast("Network request failed. Please try again.")
            }
        })
    }

    private fun fetchUserDetails(userId: String) {
        // Fetch user details using the getUser API call
        val call1 = authApi.getUser(accessToken, userId)
        call1.enqueue(object : Callback<User> {
            override fun onResponse(call1: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Update the sellerNameTv and enrollNoTv with user details
                        binding.sellerNameTv.text = "Seller: ${user.name}"
                        binding.enrollNoTv.text = "Enroll Number: ${user.enrollmentNo}"
                    } else {
                        // Handle case where user is null
                    }
                } else {
                    // Handle unsuccessful API response
                    showToast("Failed to fetch user details")

                    // Log the unsuccessful response code and message
                    Log.e("fetchUserDetails", "API call failed with code: ${response.code()}")
                    Log.e("fetchUserDetails", "API call response message: ${response.message()}")
                    // You can also log the response body if necessary: Log.e("fetchUserDetails", "API call response body: ${response.body()}")
                }
            }

            override fun onFailure(call1: Call<User>, t: Throwable) {
                // Handle API call failure
                showToast("Failed to fetch user details. Please try again.")

                // Log the error
                Log.e("fetchUserDetails", "API call failed: ${t.message}")
            }
        })
    }

    private fun displayProductData(product: Product) {
        // Display the fetched product data in the views
        // You can access the product properties like product.name, product.price, etc.
        binding.priceValue.text = product.price.toString()
        binding.dateTv.text = product.createdAt
        binding.categoryTv.text = product.category
        binding.titleTv.text = product.name
        binding.descTv.text = product.description

        // Set the first image from the images list to the ImageView
        if (!product.images.isNullOrEmpty()) {
            val firstImageUrl = product.images[0]
            // You may use an image loading library like Glide, Picasso, or Coil to load the image into the ImageView.
            // Here, I'll show you how to use Glide to load the image.
            Glide.with(this)
                .load(firstImageUrl)
                .into(binding.imageSliderVp)
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}