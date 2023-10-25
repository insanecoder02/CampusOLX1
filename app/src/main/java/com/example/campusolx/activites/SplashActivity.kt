package com.example.campusolx.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.campusolx.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the specified layout file
        setContentView(R.layout.activity_splash)

        // Hide the action bar if it is present
        supportActionBar?.hide()

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // Set flags to display the activity in full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Create a delayed action using a Handler to navigate to the LoginActivity after 3 seconds
        Handler().postDelayed({
            val i = Intent(
                this@SplashActivity,
                MainActivity2::class.java
            )
            startActivity(i)
            finish() // Finish the current SplashActivity after navigating to the LoginActivity
        }, 3000) // Delay for 3000 milliseconds (3 seconds) before starting LoginActivity
    }
}
