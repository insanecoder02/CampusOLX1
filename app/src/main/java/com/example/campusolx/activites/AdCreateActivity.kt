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
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdCreateBinding
import com.example.campusolx.dataclass.CreateProductRequest
import com.example.campusolx.dataclass.Product
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelImagePicked
import com.example.campusolx.utils.Utils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var productApi: ProductApi
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var adapterImagePicked: AdapterImagePicked
    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""

        initializeFirebaseStorage()
        setupImageRecyclerView()

        binding.addImage.setOnClickListener {
            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener {
            postAd()
        }

        binding.toolBarBackBtn.setOnClickListener{
            finish()
        }
    }

    private fun initializeFirebaseStorage() {
        FirebaseStorage.getInstance().apply {
            storageReference = reference
        }
    }

    private fun setupImageRecyclerView() {
        imagePickedArrayList = ArrayList()
        adapterImagePicked = AdapterImagePicked(this, imagePickedArrayList)

        adapterImagePicked.setOnImageLoadedListener(object :
            AdapterImagePicked.OnImageLoadedListener {
            override fun onImageLoaded(imageUri: Uri, imageView: ImageView) {
                // Handle the image loaded event here
                // You can perform any necessary actions on the loaded image
            }
        })

        binding.imagesRv.apply {
            layoutManager =
                LinearLayoutManager(this@AdCreateActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterImagePicked
        }
    }

    private fun showImagePickOptions() {
        val popupMenu = PopupMenu(this, binding.addImage)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
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
                }

                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pickImageGallery()
                    } else {
                        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                        requestStoragePermission.launch(storagePermission)
                    }
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
                Log.d(
                    "AdCreateActivity",
                    "Gallery Image URI: $imageUri"
                ) // Log the gallery image URI
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

    private fun postAd() {
        // Validate the input fields
        val brand = binding.brandEt.text.toString()
        val category = binding.categoryAct.text.toString()
        val price = binding.priceEt.text.toString()
        val title = binding.titleEt.text.toString()
        val description = binding.descEt.text.toString()

        if (imageUri != null) {
            if (brand.isNotEmpty() && category.isNotEmpty() && price.isNotEmpty() && title.isNotEmpty() && description.isNotEmpty()) {
                progressDialog.setMessage("Uploading Image...")
                progressDialog.show()

                val uploadedImageUrls = ArrayList<String>()
                val fileNames = ArrayList<String>()
                for (modelImagePicked in imagePickedArrayList) {
                    val fileName = UUID.randomUUID().toString()
                    fileNames.add(fileName)
                }

                for (i in 0 until imagePickedArrayList.size) {
                    val imageUri = imagePickedArrayList[i].imageUri
                    val fileName = fileNames[i]
                    val imageRef = storageReference.child("images/$fileName.jpg")
                    val uploadTask = imageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        progressDialog.setMessage("Uploading Image... ${i + 1}/${imagePickedArrayList.size}")

                        val downloadUrlTask = taskSnapshot.storage.downloadUrl
                        downloadUrlTask.addOnSuccessListener { downloadUri ->
                            val imageUrl = downloadUri.toString()
                            uploadedImageUrls.add(imageUrl)

                            if (uploadedImageUrls.size == imagePickedArrayList.size) {
                                val createProductRequest = CreateProductRequest(
                                    name = title,
                                    description = description,
                                    category = category,
                                    price = price.toInt(),
                                    images = uploadedImageUrls
                                )

                                createProduct(createProductRequest)
                            }
                        }
                    }.addOnFailureListener { exception ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener { taskSnapshot ->
                        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                        progressDialog.setMessage("Uploading Image... $progress%")
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createProduct(createProductRequest: CreateProductRequest) {
        val call = productApi.createProduct(accessToken, createProductRequest)

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val product = response.body()
                    Toast.makeText(this@AdCreateActivity, "Product created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(this@AdCreateActivity, "Product creation failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@AdCreateActivity, "Product creation failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}