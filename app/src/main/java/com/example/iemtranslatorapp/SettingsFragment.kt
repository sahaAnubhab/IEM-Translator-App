package com.example.iemtranslatorapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.iemtranslatorapp.databinding.FragmentHomeBinding
import com.example.iemtranslatorapp.databinding.FragmentImageToTextBinding
import com.example.iemtranslatorapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        binding.logoutButton.setOnClickListener{
            auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }
        return binding.root
    }

}