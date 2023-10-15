package com.example.campusolx.activites

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.utils.Utils
import com.example.campusolx.databinding.ActivityProfileEditBinding
import com.example.campusolx.dataclass.User
import com.example.campusolx.dataclass.UserUpdateRequest
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.RetrofitInstance
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// Define the ProfileEditActivity class
class ProfileEditActivity : AppCompatActivity() {
    // Declare variables and UI elements
    private lateinit var binding: ActivityProfileEditBinding
    private var imageUri: Uri? = null
    private lateinit var authApi: AuthApi
    private lateinit var accessToken: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the view using the layout defined in ActivityProfileEditBinding
        binding = ActivityProfileEditBinding.inflate(layoutInflater)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.statusBarColor = Color.TRANSPARENT
        setContentView(binding.root)

        // Hide the action bar if it is present
        supportActionBar?.hide()

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set flags to display the activity in full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Initialize Retrofit API instance
        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

//        val accessToken = intent.getStringExtra("accessToken")
        val name = intent.getStringExtra("name")
        val enrollmentNo = intent.getStringExtra("enrollmentNo")
//        val semester = intent.getIntExtra("semester", 0)
//        val branch = intent.getStringExtra("branch")
        val contact = intent.getStringExtra("contact")
//        val upiId = intent.getStringExtra("upiId")
        val email = intent.getStringExtra("email")
//        val userId = intent.getStringExtra("userId")
        val profilePictureUrl = intent.getStringExtra("profilePictureUrl")

        if (name != null) {
            binding.nameEt.text = Editable.Factory.getInstance().newEditable(name)
        }
        if (email != null) {
            binding.emailEt.text = Editable.Factory.getInstance().newEditable(email)
        }
        if (contact != null) {
            binding.phoneNumberEt.text = Editable.Factory.getInstance().newEditable(contact)
        }
        if (enrollmentNo != null) {
            binding.rollEt.text = Editable.Factory.getInstance().newEditable(enrollmentNo)
        }


        Glide.with(this)
            .load(profilePictureUrl)
            .placeholder(R.drawable.i2)
            .into(binding.shapeableImageView)

        binding.imagePicker.setOnClickListener {
            imagePickDialog()
        }
        binding.updateBtn.setOnClickListener {
            validateData()
        }

        // Retrieve user data from SharedPreferences
        val sharedPreference = getSharedPreferences("Account_Details", MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""
        userId = sharedPreference.getString("userId", "") ?: ""
    }

    // Declare variables to store user data
    private var name = ""
    private var rollNo = ""
    private var email = ""
    private var semester = 0
    private var branch = ""
    private var contact = ""
    private var upiId = ""
    private var profilePicture = ""

    private fun validateData() {
        val sharedPreferences = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        name = (binding.nameEt.text.toString().trim().takeIf { it.isNotBlank() } ?: sharedPreferences.getString("name", "")) as String
        rollNo = (binding.rollEt.text.toString().trim().takeIf { it.isNotBlank() } ?: sharedPreferences.getString("enrollmentNo", "")) as String
        email = (binding.emailEt.text.toString().trim().takeIf { it.isNotBlank() } ?: sharedPreferences.getString("email", "")) as String
        semester = sharedPreferences.getInt("semester", 0)
        branch = sharedPreferences.getString("branch", "").toString()
        contact = sharedPreferences.getString("contact", "").toString()
        upiId = sharedPreferences.getString("upiId", "").toString()
        profilePicture = sharedPreferences.getString("profilePictureUrl", "").toString()

        // If a new image is selected, upload it to Firebase Storage
        if (imageUri != null) {
            uploadProfilePictureToFirebase()
        } else {
            updateUser()
//            startActivity(Intent(this,req))
        }
    }

    // Function to upload a profile picture to Firebase Storage
    private fun uploadProfilePictureToFirebase() {
        if (imageUri == null) return

        // Define the Firebase Storage reference
        val storageReference = FirebaseStorage.getInstance().reference.child("profile_pictures")
        val profilePictureRef = storageReference.child(userId)

        // Create a File object from the selected image URI
        val file = File(imageUri?.path)

        // Upload the image to Firebase Storage
        val uploadTask = profilePictureRef.putFile(Uri.fromFile(file))

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image upload to Firebase Storage is successful, get the download URL
                profilePictureRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // The downloadUri contains the URL of the uploaded image
                    val downloadUrl = downloadUri.toString()
                    // Update the profilePicture field with the download URL
                    profilePicture = downloadUrl

                    // Proceed with updating the user data in the backend
                    updateUser()
                }.addOnFailureListener { exception ->
                    // Failed to get the download URL, handle the error
                    Log.e(TAG, "Failed to get download URL: ${exception.message}")
                    // You may want to show an error message to the user or handle the failure accordingly
                }
            } else {
                // Image upload to Firebase Storage failed, handle the error
                Log.e(TAG, "Image upload failed: ${task.exception?.message}")
                // You may want to show an error message to the user or handle the failure accordingly
            }
        }
    }

    // Function to update user data in the backend
    private fun updateUser() {
        val userUpdateRequest = UserUpdateRequest(
            name = name,
            semester = semester,
            upiId = upiId,
            branch = branch,
            contact = contact,
            profilePicture = profilePicture
        )

        authApi.updateUser(accessToken, userId, userUpdateRequest)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // User update successful, handle the response as needed
                        val updatedUser = response.body()
                        // Update the UI or perform any other operations with the updated user data
                    } else {
                        // User update failed, handle the error response
                        val errorBody = response.errorBody()?.string()
                        // Handle the error message appropriately
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    // Handle the failure appropriately
                }
            })
    }

    // Function to display an image pick dialog
    private fun imagePickDialog() {
        val popupMenu = PopupMenu(this, binding.changeImage)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermission.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    requestCameraPermission.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery()
                } else {
                    requestStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

    // Register permission requests for camera and storage access
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var areAllGranted = true
            for (isGranted in result.values) {
                areAllGranted = areAllGranted && isGranted
            }
            if (areAllGranted) {
                pickImageCamera()
            } else {
                Utils.toast(this, "Camera or Storage or both permissions denied")
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

    // Function to pick an image from the camera
    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_image_title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_image_description")

        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    // Function to pick an image from the gallery
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    // Register activity result launcher for camera
    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.shapeableImageView)
                } catch (e: Exception) {
                    Log.e(TAG, "cameraActivityResultLauncher", e)
                }
            } else {
                Utils.toast(this, "Cancelled")
            }
        }

    // Register activity result launcher for gallery
    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.shapeableImageView)
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "galleryActivityResultLauncher", e)
                }
            }
        }
}
