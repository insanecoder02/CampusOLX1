package com.example.campusolx

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.example.campusolx.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi

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

        // Initialize Retrofit instance and AuthApi interface
        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.loginEmail.text.toString().trim()
        password = binding.loginPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEmail.error = "Invalid Email Format"
            binding.loginEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.loginPassword.error = "Enter Password"
            binding.loginPassword.requestFocus()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.show()
//        val nEmail = "\"$email\""
//        val nPassword = "\"$password\""
//        val request = authApi.login(nEmail, nPassword)
//        Log.d("Request URL", request.request().url.toString()) // Log the request URL
//        Log.d("Request Headers", request.request().toString()) // Log the request
//        authApi.login(nEmail, nPassword).enqueue(object : Callback<AuthTokenResponse> {
        val request = LoginRequest(email, password)
        authApi.login(request).enqueue(object : Callback<AuthTokenResponse>{
            override fun onResponse(call: Call<AuthTokenResponse>, response: Response<AuthTokenResponse>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    val authToken = response.body()?.token
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error occurred"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error occurred"
                        }
                    }
                    if (errorBody != null) {
                        Log.e("LoginActivity", errorBody)
                    }
                    Toast.makeText(this@LoginActivity, "Unsuccessful: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<AuthTokenResponse>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message
                Toast.makeText(this@LoginActivity, "Unsuccessful: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}