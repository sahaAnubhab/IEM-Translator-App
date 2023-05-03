package com.example.iemtranslatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iemtranslatorapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.email.setText("sahaanubhab@gmail.com")
        binding.fullName.setText("Anubhab Saha")
        binding.mobileNumber.setText("837044852")
    }
}