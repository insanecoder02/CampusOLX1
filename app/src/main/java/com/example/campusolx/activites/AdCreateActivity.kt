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
import android.os.Environment
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
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.Console
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var productApi: ProductApi
    private var imageUri: Uri? = null
    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>
    private lateinit var adapterImagePicked: AdapterImagePicked
    private lateinit var accessToken: String

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

    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImageGallery()
            } else {
                Utils.toast(this, "Storage Permission Denied")
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
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

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                Log.d("AdCreateActivity", "Gallery Image URI: $imageUri") // Log the gallery image URI
                val timestamp = "${Utils.getTimestamp()}"
                val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
                imagePickedArrayList.add(modelImagePicked)
                adapterImagePicked.notifyDataSetChanged()
            } else {
                Utils.toast(this, "Cancelled..!")
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("AdCreateActivity", "Camera Image URI: $imageUri") // Log the camera image URI
                val timestamp = "${Utils.getTimestamp()}"
                val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
                imagePickedArrayList.add(modelImagePicked)
                adapterImagePicked.notifyDataSetChanged()
            } else {
                Utils.toast(this, "Cancelled!")
            }
        }


    private fun validateData() {
        val brand = binding.brandEt.text.toString().trim()
        val category = binding.categoryAct.text.toString().trim()
        val price = binding.priceEt.text.toString().trim()
        val title = binding.titleEt.text.toString().trim()
        val description = binding.descEt.text.toString().trim()

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
            // Handle the validation logic here
        }
    }

    private fun uploadImages() {
        val imageParts = mutableListOf<MultipartBody.Part>()

        for (modelImagePicked in imagePickedArrayList) {
            val imageUri = modelImagePicked.imageUri
            if (imageUri != null) {
                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    val tempFile = createTempFile(this, "temp_image", "jpg")
                    copyStreamToFile(inputStream, tempFile)

                    val requestFile: RequestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData(
                        "image",
                        tempFile.name,
                        requestFile
                    )
                    imageParts.add(imagePart)

                    // Log image details
                    Log.d("UploadImages", "Image URI: $imageUri")
                    Log.d("UploadImages", "Image File Type: image/*")
                    Log.d("UploadImages", "Image File Size: ${tempFile.length() / 1024} KB")

                    // Delete the temporary file
                    tempFile.delete()
                } ?: Log.e("UploadImages", "Failed to open image input stream")
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
                                    // Handle the case of successful image upload
                                    // You can perform further actions with the uploaded images
                                    Log.d("W", "Successful")

                                } else {
                                    Log.e("UploadImages", "Failed to upload images")
                                }
                            } else {
                                Log.e("UploadImages", "Failed to parse response body")
                            }
                        } else {
                            Log.e(
                                "UploadImages",
                                "Failed to upload images. Response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<UploadProductImageResponse>, t: Throwable) {
                        Log.e("UploadImages", "Failed to upload images: ${t.message}")
                    }
                })
        } else {
            // Handle the case where no images were selected
            Log.e("UploadImages", "No images selected for upload")
        }
    }

    @Throws(IOException::class)
    private fun createTempFile(context: Context, fileName: String, extension: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File(storageDir, "$fileName.$extension")
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

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
//    }



    // Make sure to replace `accessToken` with the actual access token for authorization. Also, modify the `clearForm` function according to your implementation.
//
//Please note that the code assumes that the image upload and ad update requests are handled correctly by the server, and the API responses are mapped to the respective data classes properly.