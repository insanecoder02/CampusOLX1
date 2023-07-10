package com.example.campusolx.activites

import AdapterImagePicked
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdCreateBinding
import com.example.campusolx.dataclass.CreateProductRequest
import com.example.campusolx.dataclass.CreateProductResponse
import com.example.campusolx.dataclass.Image
import com.example.campusolx.dataclass.Product
import com.example.campusolx.dataclass.UploadProductImageResponse
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelImagePicked
import com.example.campusolx.utils.Utils
import retrofit2.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var productApi: ProductApi
    private var imageUri: Uri? = null
    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>
    private lateinit var adapterImagePicked: AdapterImagePicked
    lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.toolBarBackBtn.setOnClickListener {
            onBackPressed()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        binding.toolBarBackBtn.setOnClickListener {
            onBackPressed()
        }

        imagePickedArrayList = ArrayList()
        loadImages()

        binding.addImage.setOnClickListener {
            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener {
            validateData()
            uploadImages()
        }
    }

    private fun loadImages() {
        adapterImagePicked = AdapterImagePicked(this, imagePickedArrayList)

        adapterImagePicked.setOnImageLoadedListener(object :
            AdapterImagePicked.OnImageLoadedListener {
            override fun onImageLoaded(imageUri: Uri, imageView: ImageView) {
                // Handle the image loaded event here
                // You can perform any necessary actions on the loaded image
            }
        })

        binding.imagesRv.adapter = adapterImagePicked
    }

    private fun showImagePickOptions() {
        val popupMenu = PopupMenu(this, binding.addImage)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId

            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermissions)
                } else {
                    val cameraPermissions = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestCameraPermission.launch(cameraPermissions)
                }
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery()
                } else {
                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)
                }
            }
            true
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageGallery()
        } else {
            Utils.toast(this, "Storage Permission Denied")
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var areAllGranted = true
        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }
        if (areAllGranted) {
            pickImageCamera()
        } else {
            Utils.toast(this, "Camera or Storage Permissions Both Denied")
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data!!.data
            val timestamp = "${Utils.getTimestamp()}"
            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
            imagePickedArrayList.add(modelImagePicked)
            loadImages()
        } else {
            Utils.toast(this, "Cancelled..!")
        }
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("AdCreate", "cameraActivityResultLauncher: imageUri $imageUri")
            val timestamp = "${Utils.getTimestamp()}"
            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
            imagePickedArrayList.add(modelImagePicked)
            loadImages()
        } else {
            Utils.toast(this, "Cancelled!")
        }
    }

    private var brand = ""
    private var category = ""
    private var price = ""
    private var title = ""
    private var description = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private fun validateData() {
        brand = binding.brandEt.text.toString().trim()
        category = binding.categoryAct.text.toString().trim()
        price = binding.priceEt.text.toString().trim()
        title = binding.titleEt.text.toString().trim()
        description = binding.descEt.text.toString().trim()

        if (category.isEmpty()) {
            binding.categoryAct.error = "Choose Category"
            binding.categoryAct.requestFocus()
        } else if (title.isEmpty()) {
            binding.titleEt.error = "Enter Title"
            binding.titleEt.requestFocus()
        } else if (description.isEmpty()) {
            binding.descEt.error = "Enter Description"
            binding.descEt.requestFocus()
        } else {
            uploadImages()
        }
    }

    private fun uploadImages() {
        val imageParts = mutableListOf<MultipartBody.Part>()

        for (modelImagePicked in imagePickedArrayList) {
            val imageUri = modelImagePicked.imageUri
            if (imageUri != null) {
                val imageFile = File(imageUri.path)
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
                imageParts.add(imagePart)
            }
        }

        if (imageParts.isNotEmpty()) {
            productApi.uploadProductImage(accessToken, imageParts)
                .enqueue(object : Callback<UploadProductImageResponse> {
                    override fun onResponse(
                        call: Call<UploadProductImageResponse>,
                        response: Response<UploadProductImageResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val imageResponses = responseBody.images
                                val uploadedImages = imageResponses.map { imageResponse ->
                                    Image(imageResponse.publicId, imageResponse.url)
                                }
                                if (uploadedImages.isNotEmpty()) {
                                    val adData = CreateProductRequest(
                                        name = title,
                                        description = description,
                                        category = category,
                                        prices = price.toInt(),
                                        images = uploadedImages
                                    )
                                    Toast.makeText(this@AdCreateActivity, "Affirmative", Toast.LENGTH_LONG).show()
                                    createAd(adData)
                                } else {
                                    Log.e("UploadImages", "Failed to upload images")
                                }
                            } else {
                                Log.e("UploadImages", "Failed to parse response body")
                            }
                        } else {
                            Log.e("UploadImages", "Failed to upload images")
                        }
                    }


                    override fun onFailure(call: Call<UploadProductImageResponse>, t: Throwable) {
                        Log.e("UploadImages", "Failed to upload images: ${t.message}")
                    }
                })
        }
    }


    private fun createAd(adData: CreateProductRequest) {
        progressDialog.setMessage("Publishing Ad")
        progressDialog.show()

        productApi.createProduct(accessToken = accessToken, request = adData).enqueue(object :
            Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    // API request successful, process the response
                    Utils.toast(this@AdCreateActivity, "Ad created successfully")
                } else {
                    // API request failed, handle the error
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        response.message()
                    } else {
                        // Parse the error body to extract the specific error message
                        // Use appropriate JSON parsing library like Gson or Moshi
                        // Example: val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        //         val errorMessage = errorResponse.message
                        // Replace ErrorResponse with your actual error response data class
                        "Failed to post ad: $errorBody"
                    }
                    Utils.toast(this@AdCreateActivity, errorMessage)
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                progressDialog.dismiss()
                Utils.toast(this@AdCreateActivity, "Failed to post ad: ${t.message}")
            }
        })
    }


//    private fun postAd() {
//        progressDialog.setMessage("Publishing Ad")
//        progressDialog.show()
//
//        val adData = CreateProductRequest(
//            name = title,
//            description = description,
//            category = category,
//            prices = price.toInt(),
//            images = emptyList()
//        )
//
//        productApi.createProduct(accessToken = accessToken, request = adData).enqueue(object :
//            Callback<Product> {
//            override fun onResponse(call: Call<Product>, response: Response<Product>) {
//                Utils.toast(this@AdCreateActivity, "${accessToken}")
//                progressDialog.dismiss()
//                if (response.isSuccessful) {
//                    // API request successful, process the response
//                    val adId = response.body()?._id
//                    if (adId != null) {
//                        uploadImages(adId)
//                    } else {
//                        Utils.toast(this@AdCreateActivity, "Failed to get Ad ID")
//                    }
//                } else {
//                    // API request failed, handle the error
//                    val errorBody = response.errorBody()?.string()
//                    val errorMessage = if (errorBody.isNullOrEmpty()) {
//                        response.message()
//                    } else {
//                        // Parse the error body to extract the specific error message
//                        // Use appropriate JSON parsing library like Gson or Moshi
//                        // Example: val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
//                        //         val errorMessage = errorResponse.message
//                        // Replace ErrorResponse with your actual error response data class
//                        "Failed to post ad: $errorBody"
//                    }
//                    Utils.toast(this@AdCreateActivity, errorMessage)
//                }
//            }
//
//            override fun onFailure(call: Call<Product>, t: Throwable) {
//                progressDialog.dismiss()
//                Utils.toast(this@AdCreateActivity, "Failed to post ad: ${t.message}")
//            }
//        })
//    }
//
//
//
//
//    private fun uploadImages(adId: String) {
//        val uploadedImages = mutableListOf<String>()
//
//        for (modelImagePicked in imagePickedArrayList) {
//            val imageUri = modelImagePicked.imageUri
//            if (imageUri != null) {
//                val imageFile = File(imageUri.path)
//                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
//                val imagePart =
//                    MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
//
//                // Log the upload image request
//                Utils.toast(this@AdCreateActivity,"Upload Image Request: adId=$adId, imageFile=${imageFile.name}")
//
//                productApi.uploadProductImage(accessToken, imagePart)
//                    .enqueue(object : Callback<UploadProductImageResponse> {
//                        override fun onResponse(
//                            call: Call<UploadProductImageResponse>,
//                            response: Response<UploadProductImageResponse>
//                        ) {
//                            if (response.isSuccessful) {
//                                val uploadedImage = response.body()?.images?.get(0)
//                                if (uploadedImage != null) {
//                                    uploadedImages.add(uploadedImage.url)
//
//                                    if (uploadedImages.size == imagePickedArrayList.size) {
//                                        val adData = CreateProductRequest(
//                                            name = title,
//                                            description = description,
//                                            category = category,
//                                            prices = price.toInt(),
//                                            images = uploadedImages.map { url ->
//                                                Image(publicId = uploadedImage.publicId, url = url)
//                                            }
//                                        )
//                                        updateAdWithImages(adId, adData)
//                                    }
//                                } else {
//                                    Utils.toast(this@AdCreateActivity, "Failed to upload images")
//                                }
//                            } else {
//                                Utils.toast(this@AdCreateActivity, "Failed to upload images")
//                            }
//                        }
//
//                        override fun onFailure(
//                            call: Call<UploadProductImageResponse>,
//                            t: Throwable
//                        ) {
//                            Utils.toast(
//                                this@AdCreateActivity,
//                                "Failed to upload images: ${t.message}"
//                            )
//                        }
//                    })
//            }
//        }
//    }


//    private fun updateAdWithImages(adId: String, adData: CreateProductRequest) {
//        productApi.updateProduct(accessToken = accessToken, id = adId, request = adData).enqueue(object : Callback<Product> {
//            override fun onResponse(call: Call<Product>, response: Response<Product>) {
//                progressDialog.dismiss()
//                if (response.isSuccessful) {
//                    Utils.toast(this@AdCreateActivity, "Ad posted successfully")
//                    onBackPressed()
//                } else {
//                    Utils.toast(this@AdCreateActivity, "Failed to update ad with images")
//                }
//            }
//
//            override fun onFailure(call: Call<Product>, t: Throwable) {
//                progressDialog.dismiss()
//                Utils.toast(this@AdCreateActivity, "Failed to update ad with images: ${t.message}")
//            }
//        })
//}
    }



    // Make sure to replace `accessToken` with the actual access token for authorization. Also, modify the `clearForm` function according to your implementation.
//
//Please note that the code assumes that the image upload and ad update requests are handled correctly by the server, and the API responses are mapped to the respective data classes properly.