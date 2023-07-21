package com.example.campusolx.activites

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.campusolx.R
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.dataclass.Product
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelAd
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AdActivity : AppCompatActivity() {
    private lateinit var accessToken: String
    private lateinit var productApi: ProductApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)
        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""
    }

    private fun fetchProductById(productId: String) {
        val call = productApi.getProductById(accessToken, productId)
        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val product = response.body()
                    product?.let { product ->
                        val ad = ModelAd().apply {
                            id = product._id
                            uid = product.createdBy
                            brand = product.name
                            category = product.category
                            price = product.price.toString()
                            title = product.name
                            description = product.description
                            status = if (product.isSold) "Sold" else "Available"
                            timestamp = product.createdAt?.let {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                                dateFormat.parse(it)?.time ?: 0L
                            } ?: 0L
                            latitude = 0.0
                            longitude = 0.0
                            imageList = ArrayList(product.images)
                        }

//                        // Update the adArrayList with the single product
//                        adArrayList.clear()
//                        adArrayList.add(ad)
//                        adapterAd.notifyDataSetChanged()
                    }
                } else {
                    // Handle error case
                    val errorMessage = response.message()
                    // Show an error message or handle the error response
                    // For example, you can display a toast message with the error
                    Log.e("Fetch", "Failed to fetch product: $errorMessage")
                    Toast.makeText(this@AdActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                // Handle failure case
                // Show an error message or handle the failure
                // For example, you can display a toast message with the failure
                Log.e("Fetch", "Failed to fetch product: ${t.message}")
                Toast.makeText(this@AdActivity, "Failed to fetch product: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}