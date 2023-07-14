package com.example.campusolx.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.adapters.AdapterAd
import com.example.campusolx.models.ModelAd
import com.example.campusolx.dataclass.Product
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Retrofit ProductApi
        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        // Get the access token from shared preferences
        val sharedPreference = mContext.getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        // Setup RecyclerView and Adapter
        adapterAd = AdapterAd(mContext, adArrayList)
        binding.adsRv.layoutManager = LinearLayoutManager(mContext)
        binding.adsRv.adapter = adapterAd

        // Fetch and display the products
        fetchProducts()
    }

    private fun fetchProducts() {
        val call = productApi.getAllProducts(accessToken)
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
                        adArrayList.clear()
                        for (product in it) {
                            val ad = ModelAd().apply {
                                id = product.id
                                uid = product.uid
                                brand = product.brand
                                category = product.category
                                price = product.price
                                title = product.title
                                description = product.description
                                status = product.status
                                timestamp = product.timestamp
                                latitude = product.latitude
                                longitude = product.longitude
                                imageList = ArrayList(product.imageList)
                            }
                            adArrayList.add(ad)
                        }
                        adapterAd.notifyDataSetChanged()
                    }
                } else {
                    // Handle error case
                    // Show an error message or handle the error response
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                // Handle failure case
                // Show an error message or handle the failure
            }
        })
    }
}
