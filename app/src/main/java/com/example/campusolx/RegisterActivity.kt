package com.example.campusolx

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.campusolx.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi

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

        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        binding.imageView3.setOnClickListener {
            onBackPressed()
        }
        binding.registerButton.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val name = binding.userName.text.toString().trim()
        val rollNo = binding.enrollNo.text.toString().trim()
        val semester = binding.semester.text.toString().trim()
        val branch = binding.branch.text.toString().trim()
        val contact = binding.contact.text.toString().trim()
        val email = binding.registerEmail.text.toString().trim()
        val password = binding.registerPassword.text.toString().trim()
        val upiId = binding.upiId.text.toString().trim()

        if (name.isEmpty()) {
            binding.userName.error = "Enter a valid username"
            binding.userName.requestFocus()
        } else if (rollNo.isEmpty()) {
            binding.enrollNo.error = "Enter a valid Roll no."
            binding.enrollNo.requestFocus()
        } else if (semester.isEmpty()) {
            binding.semester.error = "Enter your Semester"
            binding.semester.requestFocus()
        } else if (branch.isEmpty()) {
            binding.branch.error = "Enter your Branch"
            binding.branch.requestFocus()
        } else if (contact.isEmpty()) {
            binding.contact.error = "Enter your Contact No."
            binding.contact.requestFocus()
        } else if (upiId.isEmpty()) {
            binding.upiId.error = "Enter your UPI ID"
            binding.upiId.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmail.error = "Invalid Email Pattern"
            binding.registerEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.registerPassword.error = "Enter a valid password"
            binding.registerPassword.requestFocus()
        } else {
            registerUser(name, rollNo, semester, branch, contact, email, password, upiId)
        }
    }

    private fun registerUser(
        name: String,
        rollNo: String,
        semester: String,
        branch: String,
        contact: String,
        email: String,
        password: String,
        upiId: String
    ) {
        progressDialog.show()

        val request = RegisterRequest(name, rollNo, semester.toInt(), upiId, branch, contact, email, password)
        authApi.register(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    // Registration successful, handle the response as needed
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_LONG).show()

                    // Transition to VerifyAccountActivity
                    val intent = Intent(this@RegisterActivity, VerifyAccountActivity::class.java)
                    intent.putExtra("email", email) // Pass email as extra data
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // Registration failed, handle the error response
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
                        Log.e("RegisterActivity", errorBody)
                    }
                    Toast.makeText(this@RegisterActivity, "Registration Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message
                Toast.makeText(this@RegisterActivity, "Registration Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}