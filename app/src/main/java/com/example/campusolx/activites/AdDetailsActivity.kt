package com.example.campusolx.activites

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdDetailsBinding
import com.example.campusolx.dataclass.Product
import com.example.campusolx.interfaces.ProductApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdDetailsBinding
    private lateinit var productApi: ProductApi
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("product_id") ?: ""

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        val call = productApi.getProductById(accessToken, productId)

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
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

    private fun displayProductData(product: Product) {
        // Display the fetched product data in the views
        // You can access the product properties like product.name, product.price, etc.
        binding.priceValue.text = product.price.toString()
        binding.dateTv.text = product.createdAt
        binding.categoryTv.text = product.category
        binding.titleTv.text = product.name
        binding.descTv.text = product.description
        binding.sellerNameTv.text = "Seller: ${product.createdBy}"
        binding.enrollNoTv.text = "Enroll Number: ${product._id}"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}