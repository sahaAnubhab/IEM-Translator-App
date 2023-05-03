package com.example.iemtranslatorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.iemtranslatorapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.alreadyRegisteredButton.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.signUpButton.setOnClickListener{
            signUp()
        }


    }

    private fun signUp() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val rePassword = binding.rePassword.text.toString()
        if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        if(password != rePassword){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if(!isValidPassword(password)){
            Toast.makeText(this, "Password should contain minimum of 8 character with at least one letter, one number and one special character.", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task->
            if(task.isSuccessful){
                Log.d(LoginActivity.TAG, "signInWithEmail:success")
                val user = auth.currentUser
                updateUI(user)
            }else{
                Log.w(LoginActivity.TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    task.exception?.message.toString(),
                    Toast.LENGTH_SHORT,
                ).show()
                updateUI(null)
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            Toast.makeText(this, "An email verification mail has been sent to your email id. Please verify your email id.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }
}