package com.example.campusolx.activites

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.utils.Utils
import com.example.campusolx.databinding.ActivityProfileEditBinding
import com.example.campusolx.dataclass.User
import com.example.campusolx.dataclass.UserUpdateRequest
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private var imageUri: Uri? = null
    private lateinit var authApi: AuthApi
    private lateinit var accessToken: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )



        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        binding.toolBarBackBtn.setOnClickListener {
            onBackPressed()
        }
        binding.profileImagePickFab.setOnClickListener {
            imagePickDialog()
        }
        binding.updateBtn.setOnClickListener {
            validateData()
        }
        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        accessToken = "Bearer " + sharedPreference.getString("accessToken", "") ?: ""
        userId = sharedPreference.getString("userId", "") ?: ""
    }

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
        semester = sharedPreferences.getInt("semesterbinding.branchEt.text.toString().trim().takeIf { it.isNotBlank() } ?: ", 0)
        branch = sharedPreferences.getString("branch", "").toString()
        contact = sharedPreferences.getString("contact", "").toString()
        upiId = sharedPreferences.getString("upiId", "").toString()
        profilePicture = sharedPreferences.getString("profilePictureUrl", "").toString()

        updateUser()
    }

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
//                        val updatedUser = response.body()
                        val updatedUser = response.body()

                        val name = updatedUser?.name
                        val enrollmentNo = updatedUser?.enrollmentNo
                        val semester = updatedUser?.semester
                        val branch = updatedUser?.branch
                        val contact = updatedUser?.contact
                        val upiId = updatedUser?.upiId
                        val email = updatedUser?.email
                        val profilePictureUrl = updatedUser?.profilePicture

                        val sharedPreference = getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()

                        editor.putString("name", name)
                        editor.putString("enrollmentNo", enrollmentNo)
                        if (semester != null) {
                            editor.putInt("semester", semester)
                        }
                        editor.putString("branch", branch)
                        editor.putString("contact", contact)
                        editor.putString("upiId", upiId)
                        editor.putString("email", email)
                        editor.putString("profilePictureUrl", profilePictureUrl)

                        editor.apply()

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

    private fun imagePickDialog() {
        val popupMenu = PopupMenu(this, binding.profileImagePickFab)
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

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.profileTv)
                } catch (e: Exception) {
                    Log.e(TAG, "cameraActivityResultLauncher", e)
                }
            } else {
                Utils.toast(this, "Cancelled")
            }
        }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.profileTv)
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "galleryActivityResultLauncher", e)
                }
            }
        }
}
