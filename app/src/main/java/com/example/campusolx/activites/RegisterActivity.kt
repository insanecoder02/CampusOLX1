package com.example.campusolx.activites

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.dataclass.RegisterRequest
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityRegisterBinding
import com.example.campusolx.utils.LoadingUtils
import com.example.campusolx.utils.RegisterLoadingUtils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Define the RegisterActivity class
class RegisterActivity : AppCompatActivity() {

    // Declare variables and UI elements
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the view using the layout defined in ActivityRegisterBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar if it is present
        supportActionBar?.hide()

        // Set night mode to dark
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set flags to display the activity in full-screen mode
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        // Initialize a ProgressDialog for later use
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(true)

        // Initialize Retrofit API instance
        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        // Set click listeners for UI elements
        binding.backbutt.setOnClickListener {
            onBackPressed()
        }
        binding.registerButton.setOnClickListener {
            validateData()
        }
    }

    // Function to validate user registration data
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
            // If data is valid, register the user
            registerUser(name, rollNo, semester, branch, contact, email, password, upiId)
        }
    }

    // Function to register the user with provided data
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
        // Show a loading dialog while registering
        RegisterLoadingUtils.showDialog(this, true)

        val request = RegisterRequest(name, rollNo, semester.toInt(), upiId, branch, contact, email, password)
        authApi.register(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Hide the loading dialog
                RegisterLoadingUtils.hideDialog()

                if (response.isSuccessful) {
                    // Registration successful, handle the response as needed
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_LONG).show()

                    // Transition to VerifyAccountActivity and pass email as extra data
                    val intent = Intent(this@RegisterActivity, VerifyAccountActivity::class.java)
                    intent.putExtra("email", email)
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
                // Hide the loading dialog and show an error message
                RegisterLoadingUtils.hideDialog()
                val errorMessage = t.message
                Toast.makeText(this@RegisterActivity, "Registration Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}
