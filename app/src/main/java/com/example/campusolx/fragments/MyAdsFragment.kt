package com.example.campusolx.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.adapters.AdapterAd
import com.example.campusolx.dataclass.Product
import com.example.campusolx.databinding.FragmentMyAdsBinding
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelAd
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class MyAdsFragment : Fragment() {
    private lateinit var binding: FragmentMyAdsBinding
    private lateinit var mContext: Context
    private lateinit var productApi: ProductApi
    private lateinit var adapterAd: AdapterAd
    private var adArrayList: ArrayList<ModelAd> = ArrayList()
    private lateinit var accessToken: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Retrofit ProductApi
        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        // Get the access token from shared preferences
        val sharedPreference =
            mContext.getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        // Setup RecyclerView and Adapter
        adapterAd = AdapterAd(mContext, adArrayList)
        binding.adsRv.layoutManager = LinearLayoutManager(mContext)
        binding.adsRv.adapter = adapterAd

        // Fetch and display the products of the current user
        fetchMyAds()
    }

    private fun fetchMyAds() {
        val call = productApi.getAllProductsOfUser(accessToken)
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let { products ->
                        adArrayList.clear()
                        for (product in products) {
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
                            adArrayList.add(ad)
                        }
                        adapterAd.notifyDataSetChanged()
                    }
                } else {
                    // Handle error case
                    val errorMessage = response.message()
                    // Show an error message or handle the error response
                    // For example, you can display a toast message with the error
                    Log.e("Fetch", "Failed to fetch products: $errorMessage")
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                // Handle failure case
                // Show an error message or handle the failure
                // For example, you can display a toast message with the failure
                Log.e("Fetch", "Failed to fetch products: ${t.message}")
                Toast.makeText(
                    context,
                    "Failed to fetch products: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
