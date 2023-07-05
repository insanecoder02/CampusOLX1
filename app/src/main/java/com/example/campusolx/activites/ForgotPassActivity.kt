package com.example.campusolx.activites

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.dataclass.ForgotPasswordRequest
import com.example.campusolx.R
import com.example.campusolx.RetrofitInstance
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var submitButton: MaterialButton
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        emailEditText = findViewById(R.id.newPasswordEt2)
        submitButton = findViewById(R.id.submitButtonForgot)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(true)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (isValidEmail(email)) {
                sendForgotPasswordRequest(email)
            } else {
                emailEditText.error = "Invalid Email"
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun sendForgotPasswordRequest(email: String) {
        progressDialog.show()

        val request = ForgotPasswordRequest(email)
        authApi.forgotPassword(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    // Forgot password request successful, handle the response as needed
                    Toast.makeText(this@ForgotPassActivity, "Forgot Password Request Successful", Toast.LENGTH_LONG).show()

                    // Transition to ResetPasswordActivity
                    val intent = Intent(this@ForgotPassActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email) // Pass email as extra data
                    startActivity(intent)
                    finish()
                } else {
                    // Forgot password request failed, handle the error response
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
                        Log.e("ForgotPassActivity", errorBody)
                    }
                    Toast.makeText(this@ForgotPassActivity, "Forgot Password Request Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message
                Toast.makeText(this@ForgotPassActivity, "Forgot Password Request Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}