package com.example.campusolx.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.campusolx.fragments.AccountFragment
import com.example.campusolx.fragments.HomeFragment
import com.example.campusolx.fragments.MyAdsFragment
import com.example.campusolx.R
import com.example.campusolx.fragments.SettingsFragment
import com.example.campusolx.databinding.ActivityMainBinding

// Define the MainActivity class
class MainActivity : AppCompatActivity() {
    // Declare the view binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the view using the layout defined in ActivityMainBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar if it is present
        supportActionBar?.hide()

        // Set flags to display the activity in full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set click listener for bottom navigation items
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    showHomeFragment()
                    true
                }
                R.id.menu_account -> {
                    showAccountFragment()
                    true
                }
                R.id.menu_my_ads -> {
                    showMyAdsFragment()
                    true
                }
                R.id.menu_settings -> {
                    showSettingsFragment()
                    true
                }
                R.id.menu_sell -> {
                    // Start the AdCreateActivity when "Sell" is selected
                    startActivity(Intent(this, AdCreateActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Set a click listener for a button (assuming you have a button with id "button" in your layout)
        binding.button.setOnClickListener {
            // Start the AdCreateActivity when the button is clicked
            startActivity(Intent(this, AdCreateActivity::class.java))
        }

        // Show the HomeFragment by default when the activity is created
        showHomeFragment()
    }

    // Function to show the HomeFragment
    private fun showHomeFragment() {
        // Set the toolbar title
        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    // Function to show the AccountFragment
    private fun showAccountFragment() {
        // Set the toolbar title
        val fragment = AccountFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "AccountFragment")
        fragmentTransaction.commit()
    }

    // Function to show the MyAdsFragment
    private fun showMyAdsFragment() {
        // Set the toolbar title
         val fragment = MyAdsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "MyAdsFragment")
        fragmentTransaction.commit()
    }

    // Function to show the SettingsFragment
    private fun showSettingsFragment() {
        // Set the toolbar title
        val fragment = SettingsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "SettingsFragment")
        fragmentTransaction.commit()
    }
}