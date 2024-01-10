package com.example.campusolx.activites

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.campusolx.interfaces.AuthApi
import com.example.campusolx.R
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.databinding.ActivityVerifyAccountBinding
import com.example.campusolx.dataclass.VerifyRequest
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyAccountActivity : AppCompatActivity() {
    private lateinit var authApi: AuthApi
    private lateinit var progressDialog: ProgressDialog
    private lateinit var codeEditText: EditText
    private lateinit var binding : ActivityVerifyAccountBinding
    private lateinit var submitButton: MaterialButton
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize Retrofit API instance
        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)
        progressDialog = ProgressDialog(this)

        val alpha : String  = binding.digit1.text.toString() + binding.digit2.text.toString() + binding.digit3.text.toString() + binding.digit4.text.toString() + binding.digit5.text.toString() + binding.digit6.text.toString()
        // Initialize UI elements
//        codeEditText = findViewById(R.id.newPasswordEt)
//        submitButton = findViewById(R.id.submitButtonVerify)

        // Retrieve the email passed from the previous activity
        email = intent.getStringExtra("email") ?: ""

        // Set click listener for the submit button
        binding.submitButtonVerify.setOnClickListener {
            val code = alpha
            // Call the verification function with the entered code and email
            verifyAccount(email, code)
        }

        binding.toolBarBackBtn.setOnClickListener{
            finish()
        }
    }

    // Function to verify the account with the provided code and email
    private fun verifyAccount(code: String, email:String) {
        progressDialog.show()

        val request = VerifyRequest(code, email)
        authApi.verify(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    // Verification successful, handle the response as needed
                    Toast.makeText(this@VerifyAccountActivity, "Account Verified", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@VerifyAccountActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // Verification failed, handle the error response
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
                        Log.e("VerifyAccountActivity", errorBody)
                    }
                    Toast.makeText(this@VerifyAccountActivity, "Verification Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message
                // Handle the failure to make the verification request
                Toast.makeText(this@VerifyAccountActivity, "Verification Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}
