package com.example.campusolx

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
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
    private lateinit var submitButton: MaterialButton
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_account)

        val retrofit = RetrofitInstance.getRetrofitInstance()
        authApi = retrofit.create(AuthApi::class.java)
        progressDialog = ProgressDialog(this)

        codeEditText = findViewById(R.id.newPasswordEt)
        submitButton = findViewById(R.id.submitButtonVerify)

        email = intent.getStringExtra("email") ?: ""

        submitButton.setOnClickListener {
            val code = codeEditText.text.toString()
            verifyAccount(email, code)
        }
    }

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
                Toast.makeText(this@VerifyAccountActivity, "Verification Failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}