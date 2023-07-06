package com.example.campusolx.activites
import AdapterImagePicked
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.media.tv.AdResponse
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdCreateBinding
import com.example.campusolx.databinding.ActivityLoginBinding
import com.example.campusolx.databinding.ActivityMainBinding
import com.example.campusolx.dataclass.CreateProductRequest
import com.example.campusolx.dataclass.CreateProductResponse
import com.example.campusolx.dataclass.Product
import com.example.campusolx.dataclass.ProductResponse
import com.example.campusolx.dataclass.UploadProductImageResponse
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelImagePicked
import com.example.campusolx.utils.Utils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var productApi: ProductApi
    private var imageUri: Uri? = null
    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>
    private lateinit var adapterImagePicked: AdapterImagePicked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.toolBarBackBtn.setOnClickListener{
            onBackPressed()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        binding.toolBarBackBtn.setOnClickListener{
            onBackPressed()
        }

        imagePickedArrayList = ArrayList()
        loadImages()

        binding.addImage.setOnClickListener {
            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener{
            validateData()
        }
    }

    private fun loadImages(){
        adapterImagePicked = AdapterImagePicked(this, imagePickedArrayList)

        adapterImagePicked.setOnImageLoadedListener(object : AdapterImagePicked.OnImageLoadedListener {
            override fun onImageLoaded(imageUri: Uri, imageView: ImageView) {
                // Handle the image loaded event here
                // You can perform any necessary actions on the loaded image
            }
        })

        binding.imagesRv.adapter = adapterImagePicked
    }

    private fun  showImagePickOptions(){
        val popupMenu = PopupMenu(this, binding.addImage)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId

            if(itemId == 1){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermissions)
                } else {
                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestCameraPermission.launch(cameraPermissions)
                }
            } else if(itemId == 2){
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
                    pickImageGallery()
                } else
                {
                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)
                }
            }
            true
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            pickImageGallery()
        } else {
            Utils.toast(this, "Storage Permission Denied")
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ result ->
        var areAllGranted = true
        for(isGranted in result.values){
            areAllGranted = areAllGranted && isGranted
        }
        if(areAllGranted){
            pickImageCamera()
        } else {
            Utils.toast(this, "Camera or Storage Permissions Both Denied")
        }
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            imageUri = data!!.data
            val timestamp = "${Utils.getTimestamp()}"
            val modelImagePicked = ModelImagePicked(timestamp,imageUri,null,false)
            imagePickedArrayList.add(modelImagePicked)
            loadImages()
        } else {
            Utils.toast(this, "Cancelled..!")
        }
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            Log.d("AdCreate", "cameraActivityResultLauncher: imageUri $imageUri")
            val timestamp = "${Utils.getTimestamp()}"
            val modelImagePicked = ModelImagePicked(timestamp,imageUri,null,false)
            imagePickedArrayList.add(modelImagePicked)
            loadImages()
        } else {
            Utils.toast(this, "Cancelled!")
        }
    }

    private var brand = ""
    private var category =""
    private var price = ""
    private var title = ""
    private var description = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private fun validateData(){
        brand = binding.brandEt.text.toString().trim()
        category = binding.categoryAct.text.toString().trim()
        price = binding.priceEt.text.toString().trim()
        title = binding.titleEt.text.toString().trim()
        description = binding.descEt.text.toString().trim()

        if(brand.isEmpty()){
            binding.brandEt.error = "Enter Brand"
            binding.brandEt.requestFocus()
        }
        else if(category.isEmpty()){
            binding.categoryAct.error = "Choose Category"
            binding.categoryAct.requestFocus()
        }
        else if( title.isEmpty()){
            binding.titleEt.error = "Enter Title"
            binding.titleEt.requestFocus()
        }
        else if(description.isEmpty()){
            binding.descEt.error = "Enter Description"
            binding.descEt.requestFocus()
        }
        else{
            //postAd()
            Utils.toast(this,"Successful")
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
//            images = emptyList() // Updated later during image upload
//        )
//
//        productApi.createProduct(accessToken = , adData).enqueue(object : Callback<CreateProductResponse> {
//            override fun onResponse(call: Call<CreateProductResponse>, response: Response<CreateProductResponse>) {
//                progressDialog.dismiss()
//                if (response.isSuccessful) {
//                    val adId = response.body()?.product?.id
//                    if (adId != null) {
//                        uploadImages(adId)
//                    } else {
//                        Utils.toast(this@AdCreateActivity, "Failed to get Ad ID")
//                    }
//                } else {
//                    Utils.toast(this@AdCreateActivity, "Failed to post ad")
//                }
//            }
//
//            override fun onFailure(call: Call<CreateProductResponse>, t: Throwable) {
//                progressDialog.dismiss()
//                Utils.toast(this@AdCreateActivity, "Failed to post ad: ${t.message}")
//            }
//        })
//    }
//
//    private fun uploadImages(adId: String) {
//        for (modelImagePicked in imagePickedArrayList) {
//            val imageUri = modelImagePicked.imageUri
//            if (imageUri != null) {
//                val imageFile = File(imageUri.path)
//                val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
//                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
//
//                productApi.uploadProductImage(accessToken, imagePart).enqueue(object : Callback<UploadProductImageResponse> {
//                    override fun onResponse(call: Call<UploadProductImageResponse>, response: Response<UploadProductImageResponse>) {
//                        // Handle the image upload response
//                        if (response.isSuccessful) {
//                            val uploadedImages = response.body()?.images
//                            if (uploadedImages != null) {
//                                // Update the ad data with the uploaded image URLs
//                                val adData = CreateProductRequest(
//                                    name = title,
//                                    description = description,
//                                    category = category,
//                                    prices = price.toInt(),
//                                    images = uploadedImages.map { image ->
//                                        com.example.campusolx.dataclass.Image(url = image.url)
//                                    }
//                                )
//                                updateAdWithImages(adId, adData)
//                            } else {
//                                Utils.toast(this@AdCreateActivity, "Failed to upload images")
//                            }
//                        } else {
//                            Utils.toast(this@AdCreateActivity, "Failed to upload images")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<UploadProductImageResponse>, t: Throwable) {
//                        // Handle the image upload failure
//                        Utils.toast(this@AdCreateActivity, "Failed to upload images: ${t.message}")
//                    }
//                })
//            }
//        }
//    }
//
//    private fun updateAdWithImages(adId: String, adData: CreateProductRequest) {
//        productApi.updateProduct(accessToken, adId, adData).enqueue(object : Callback<ProductResponse> {
//            override fun onResponse(
//                call: Call<ProductResponse>,
//                response: Response<ProductResponse>
//            ) {
//// Handle the    response for updating the ad with images
//
//                progressDialog.dismiss()
//                if (response.isSuccessful) {
//                    Utils.toast(this@AdCreateActivity, "Ad posted successfully")
//// Clear the form and go back to the previous activity
//                    clearForm()
//                    onBackPressed()
//                } else {
//                    Utils.toast(this@AdCreateActivity, "Failed to update ad with images")
//                }
//            }
//            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
//                progressDialog.dismiss()
//                Utils.toast(this@AdCreateActivity, "Failed to update ad with images: ${t.message}")
//            }
//        })}

    // Make sure to replace `accessToken` with the actual access token for authorization. Also, modify the `clearForm` function according to your implementation.
//
//Please note that the code assumes that the image upload and ad update requests are handled correctly by the server, and the API responses are mapped to the respective data classes properly.