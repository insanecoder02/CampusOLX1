package com.example.campusolx.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.campusolx.databinding.ActivityAdCreateBinding
import com.example.campusolx.databinding.ActivityLoginBinding
import com.example.campusolx.databinding.ActivityMainBinding

class AdCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.toolBarBackBtn.setOnClickListener{
            onBackPressed()
        }
    }
}