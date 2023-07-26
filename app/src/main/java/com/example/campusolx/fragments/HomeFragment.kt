package com.example.campusolx.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.utils.Utils
import com.example.campusolx.R
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.activites.AdDetailsActivity
import com.example.campusolx.adapters.AdapterAd
import com.example.campusolx.adapters.AdapterCategory
import com.example.campusolx.models.ModelAd
import com.example.campusolx.dataclass.Product
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.databinding.FragmentHomeBinding
import com.example.campusolx.interfaces.RvListenerCategory
import com.example.campusolx.models.ModelCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), AdapterAd.OnAdClickListener {

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

        // Set the listener for click events
        adapterAd.setOnAdClickListener(this)
        loadCategories()
        // Fetch and display the products
        fetchProducts()
    }

    override fun onAdClick(productId: String) {
        // Create an intent to open the AdDetailsActivity
        val intent = Intent(mContext, AdDetailsActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    private fun loadCategories(){

        val categories = arrayOf(
            "Coolers",
            "Electronics",
            "Furniture",
            "Books",
            "Sports",
            "Nitish Bharat",
            "Fashion",
            "Girlfriends"
        )
        val categoryIcons = arrayOf(
            R.drawable.ic_phone,
            R.drawable.baseline_electrical_services_24,
            R.drawable.baseline_bed_24,
            R.drawable.ic_book,
            R.drawable.baseline_sports_handball_24,
            R.drawable.ic_person,
            R.drawable.ic_chat,
            R.drawable.baseline_girl_24
        )

        val categoryArrayList = ArrayList<ModelCategory>()
        for(i in 0 until categories.size){
            val modelCategory = ModelCategory(categories[i], categoryIcons[i])
            categoryArrayList.add(modelCategory)
        }
        val adapterCategory = AdapterCategory(mContext, categoryArrayList, object: RvListenerCategory{
            override fun onCategoryClick(modelCategory: ModelCategory) {
            }
        })
    }

    private fun fetchProducts() {
        com.example.campusolx.utils.AdLoader.showDialog(mContext, isCancelable = true)
        val call = productApi.getAllProducts(accessToken)
        call.enqueue(object : Callback<List<Product>> {
            @SuppressLint("NewApi")
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                com.example.campusolx.utils.AdLoader.hideDialog()
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
                com.example.campusolx.utils.AdLoader.hideDialog()
                // Handle failure case
                // Show an error message or handle the failure
                // For example, you can display a toast message with the failure
                Log.e("Fetch", "Failed to fetch products: ${t.message}")
                Toast.makeText(context, "Failed to fetch products: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
