package com.example.campusolx

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.campusolx.databinding.ActivityProfileEditBinding
import java.lang.Exception
import java.util.jar.Manifest

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileEditBinding
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.toolBarBackBtn.setOnClickListener{
            onBackPressed()
        }
        binding.profileImagePickFab.setOnClickListener{
            imagePickDialog()
        }
        binding.updateBtn.setOnClickListener{
            validateData()
        }

    }
    private var name = ""
    private var rollno = ""
    private var email = ""
    private var phoneCode = ""
    private var phoneNumber = ""


    private fun validateData(){
        name = binding.nameEt.text.toString().trim()
        rollno = binding.rollEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        phoneCode = binding.countryCodePicker.selectedCountryCodeWithPlus
        phoneNumber = binding.phoneNumberEt.text.toString().trim()


    }


    private fun imagePickDialog(){
        val popupMenu = PopupMenu(this, binding.profileImagePickFab)
        popupMenu.menu.add(Menu.NONE,1,1,"Camera")
        popupMenu.menu.add(Menu.NONE,2,2,"Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {item->
            val itemId = item.itemId
            if(itemId==1){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                    requestCameraPermission.launch(arrayOf(android.Manifest.permission.CAMERA))
                }
                else{
                    requestCameraPermission.launch(arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
            else if(itemId==2){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                    pickImageGallery()
                }
                else{
                    requestStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }

            return@setOnMenuItemClickListener true

        }

    }
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){result->
        var areAllGranted = true
        for (isGranted in result.values){
            areAllGranted = areAllGranted && isGranted
        }
        if(areAllGranted){
            pickImageCamera()
        } else{
            Utils.toast(this,"Camera or Storage or both permissions denied")

        }



    }
    private val requestStoragePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
        if(isGranted){
            pickImageGallery()
        }
        else{
            Utils.toast(this,"Storage Permission Denied")

        }
    }
    private fun pickImageCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_image_title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_image_description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }
    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }
    private val cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            try{
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.profileTv)
            }
            catch (e: Exception){
                Log.e(TAG,"cameraActivityResultLauncher",e)
            }
        }
        else{
            Utils.toast(this,"Cancelled")
        }
    }
    private val galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            imageUri = data!!.data
            try{
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.profileTv)
            }
            catch (e: java.lang.Exception){
                Log.e(TAG, "galleryActivityResultLauncher",e)
            }
        }
    }
}