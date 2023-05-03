package com.example.iemtranslatorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iemtranslatorapp.databinding.ActivityProfileUpdatePageBinding

class ProfileUpdatePage : AppCompatActivity() {

    private lateinit var binding : ActivityProfileUpdatePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdatePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}