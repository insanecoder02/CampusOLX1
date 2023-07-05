package com.example.campusolx.activites

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.R
import com.example.campusolx.dataclass.ResetPasswordRequest
import com.example.campusolx.RetrofitInstance
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var submitButton: MaterialButton
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authApi: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        currentPasswordEditText = findViewById(R.id.currentPasswordEt3)
        newPasswordEditText = findViewById(R.id.newPasswordEt3)
        submitButton = findViewById(R.id.submitButton3)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)

        val email = intent.getStringExtra("email") ?: ""

        submitButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                resetPassword(email, currentPassword, newPassword)
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String, currentPassword: String, newPassword: String) {
        progressDialog.show()

        val request = ResetPasswordRequest(email, currentPassword, newPassword)
        authApi.resetPassword(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    // Reset password successful, handle the response as needed
                    Toast.makeText(this@ResetPasswordActivity, "Password Reset Successful", Toast.LENGTH_LONG).show()

                    // Transition to LoginActivity
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // Reset password failed, handle the error response
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
                        Log.e("ResetPasswordActivity", errorBody)
                    }
                    Toast.makeText(this@ResetPasswordActivity, "Password Reset Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message
                Toast.makeText(this@ResetPasswordActivity, "Password Reset Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}