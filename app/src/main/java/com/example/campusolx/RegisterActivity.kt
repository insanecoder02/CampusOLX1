package com.example.campusolx

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.campusolx.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(true)

        binding.imageView3.setOnClickListener{
            onBackPressed()
        }
        binding.registerButton.setOnClickListener{
            validateData()
        }


    }
    private var name =""
    private var rollno = ""
    private var semester = ""
    private var branch = ""
    private var contact = ""
    private var email = ""
    private var password = ""

    private fun validateData(){
        email = binding.registerEmail.text.toString().trim()
        password = binding.registerPassword.text.toString().trim()
        name = binding.userName.text.toString().trim()
        rollno=binding.enrollNo.text.toString().trim()
        branch=binding.branch.text.toString().trim()
        semester=binding.semester.text.toString().trim()
        contact = binding.contact.text.toString().trim()

        if(name.isEmpty()){
            binding.userName.error="Enter a valid username"
            binding.userName.requestFocus()
        }
        else if(rollno.isEmpty()){
            binding.enrollNo.error="Enter a valid Roll no."
            binding.enrollNo.requestFocus()
        }
        else if(semester.isEmpty()){
            binding.semester.error="Enter your Semester"
            binding.semester.requestFocus()
        }
        else if(branch.isEmpty()){
            binding.branch.error="Enter your Branch"
            binding.branch.requestFocus()
        }

        else if(contact.isEmpty()){
            binding.contact.error="Enter your Contact No."
            binding.contact.requestFocus()
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.registerEmail.error = "Invalid Email Pattern"
            binding.registerEmail.requestFocus()
        }

        else if(password.isEmpty()){
            binding.registerPassword.error="Enter a valid password"
            binding.registerPassword.requestFocus()
        }
        else{
            registerUser()
        }

    }

    private fun registerUser(){
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}