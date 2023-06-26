package com.example.campusolx

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import com.example.campusolx.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(true)
        binding.registerLink.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener{
            validateData()
        }

    }
    private var email = ""
    private var password = ""
    private fun validateData(){
        email = binding.loginEmail.text.toString().trim()
        password = binding.loginPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.loginEmail.error = "Invaild Email Format"
            binding.loginEmail.requestFocus()
        }
        else if (password.isEmpty()){
            binding.loginPassword.error = "Enter Password"
            binding.loginPassword.requestFocus()
        }
        else{
            loginUser()
        }
    }
    private fun loginUser(){
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}