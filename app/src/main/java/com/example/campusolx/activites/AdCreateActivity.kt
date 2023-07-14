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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityAdCreateBinding
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelImagePicked
import com.example.campusolx.utils.Utils
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

        initializeFirebaseStorage()
        setupImageRecyclerView()

        binding.addImage.setOnClickListener {
            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener {
            postAd()
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
            layoutManager = LinearLayoutManager(this@AdCreateActivity, LinearLayoutManager.HORIZONTAL, false)
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
        // Check if an image is selected
        if (imageUri != null) {
            progressDialog.setMessage("Uploading Image...")
            progressDialog.show()

            // Generate a unique file name for the image
            val fileName = UUID.randomUUID().toString()

            // Get the reference to the Firebase Storage location where the image will be uploaded
            val imageRef = storageReference.child("images/$fileName.jpg")

            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putFile(imageUri!!)

            // Register callbacks to track the upload progress and handle success/failure
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image upload successful
                progressDialog.dismiss()

                // Get the download URL of the uploaded image
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { downloadUri ->
                    // Handle the download URL here
                    val imageUrl = downloadUri.toString()
                    // You can use the image URL to save it in your database or perform any further actions

                    // TODO: Add your code here to handle the uploaded image URL as required
                    // For example, you can pass the imageUrl to another function or store it in a database
                    handleUploadedImageUrl(imageUrl)
                }
            }.addOnFailureListener { exception ->
                // Image upload failed
                progressDialog.dismiss()
                Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { taskSnapshot ->
                // Update the progress of the image upload
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                progressDialog.setMessage("Uploading Image... $progress%")
            }
        } else {
            // No image selected
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleUploadedImageUrl(imageUrl: String) {
        // TODO: Implement your logic to handle the uploaded image URL
        // You can pass it to another function, store it in a database, or use it as needed
        Toast.makeText(this, "Image uploaded successfully: $imageUrl", Toast.LENGTH_SHORT).show()
    }
}
