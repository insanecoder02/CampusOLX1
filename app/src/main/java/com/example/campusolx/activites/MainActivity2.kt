package com.example.campusolx.activites

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityMain2Binding
import com.example.campusolx.dataclass.AuthTokenResponse
import com.example.campusolx.dataclass.LoginRequest
import com.example.campusolx.dataclass.RegisterRequest
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.utils.AdLoader
import com.example.campusolx.utils.RegisterLoadingUtils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.statusBarColor = Color.TRANSPARENT
        setContentView(binding.root)
// Hide the action bar
//        supportActionBar?.hide()

        // Set night mode to "YES"
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the window flags for fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(true)
        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        var isLoginScreen = true
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            validateData()
        }
        binding.reg.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private var email = ""
    private var password = ""

            // Function to validate user input data
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

            // Function to handle user login
            private fun loginUser() {
                // Show a loading dialog
                AdLoader.showDialog(this, isCancelable = true)

                // Create a LoginRequest object with user's email and password
                val request = LoginRequest(email, password)

                // Make a login API call
                authApi.login(request).enqueue(object : Callback<AuthTokenResponse> {
                    override fun onResponse(
                        call: Call<AuthTokenResponse>,
                        response: Response<AuthTokenResponse>
                    ) {
                        // Hide the loading dialog
                        AdLoader.hideDialog()

                        if (response.isSuccessful) {
                            // Extract user data and access token from the response
                            val authToken = response.body()?.token
                            val name = response.body()?.name
                            val enrollmentNo = response.body()?.enrollmentNo
                            val semester = response.body()!!.semester
                            val branch = response.body()?.branch
                            val userId = response.body()?.userId
                            val contact = response.body()?.contact
                            val upiId = response.body()?.upiId
                            val email = response.body()?.email
                            val profilePictureUrl = response.body()?.profilePicture

                            // Store user data in SharedPreferences
                            val sharedPreference =
                                getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
                            val editor = sharedPreference.edit()

                            editor.putString("accessToken", authToken)
                            editor.putString("name", name)
                            editor.putString("enrollmentNo", enrollmentNo)
                            editor.putInt("semester", semester)
                            editor.putString("branch", branch)
                            editor.putString("contact", contact)
                            editor.putString("upiId", upiId)
                            editor.putString("email", email)
                            editor.putString("userId", userId)
                            editor.putString("profilePictureUrl", profilePictureUrl)

                            editor.apply()
1
                            // Redirect to the MainActivity upon successful login
                            val intent = Intent(this@MainActivity2, ProfileEditActivity::class.java)
                            intent.putExtra("name", name)
                            intent.putExtra("enrollmentNo", enrollmentNo)
                            intent.putExtra("contact", contact)
                            intent.putExtra("email", email)
                            intent.putExtra("profilePictureUrl", profilePictureUrl)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            // Handle login failure and display an error message
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
                            Toast.makeText(
                                this@MainActivity2,
                                "Unsuccessful: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<AuthTokenResponse>, t: Throwable) {
                        // Hide the loading dialog and display an error message for failure
                        AdLoader.hideDialog()
                        val errorMessage = t.message
                        Toast.makeText(
                            this@MainActivity2,
                            "Unsuccessful: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
}
