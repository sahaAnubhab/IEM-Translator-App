package com.example.iemtranslatorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.iemtranslatorapp.databinding.ActivityProfileUpdatePageBinding
import com.example.iemtranslatorapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileUpdatePage : AppCompatActivity() {

    private lateinit var binding : ActivityProfileUpdatePageBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdatePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val db = Firebase.firestore
        binding.email.setText(auth.currentUser!!.email)

        binding.saveButton.setOnClickListener{
                val user = User(
                    auth.currentUser!!.uid,
                    binding.fullName.text.toString(),
                    binding.email.text.toString(),
                    binding.mobileNumber.text.toString()
                )
                db.collection("users")
                    .document(auth.currentUser!!.uid).set(user)
                    .addOnSuccessListener {
                        updateUI()
                        Log.d(TAG, "DocumentSnapshot added with ID")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

        }

    }

    private fun updateUI() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    companion object{
        val TAG = "ProfileUpdatePage()"
    }
}