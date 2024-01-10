package com.example.campusolx.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.campusolx.databinding.ActivityDeleteBinding

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackBtn.setOnClickListener{
            finish()
        }


    }
}