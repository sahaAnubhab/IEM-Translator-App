package com.example.iemtranslatorapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.iemtranslatorapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth;
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.noAccountButton.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.signInButton.setOnClickListener{
            signIn()
        }

        binding.signInWithGoogle.setOnClickListener{
            Log.d(TAG, "Login Clicked")
            signInUserWithGoogleSignIn()
        }

    }

    private fun signIn() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    auth.currentUser?.sendEmailVerification()
//                        ?.addOnSuccessListener {
//                            Toast.makeText(this, "Please Verify E-mail", Toast.LENGTH_SHORT).show()
//
//                        }
//                        ?.addOnFailureListener {
//                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//                        }

                    if(auth.currentUser!!.isEmailVerified){
                        updateUI(auth.currentUser)
                    }else{
                        Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user : FirebaseUser?){
        if(user != null){
            db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
                val doc = it.result
                if (doc.exists()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                }else{
                    startActivity(Intent(this, ProfileUpdatePage::class.java))
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null) {
            if (currentUser.isEmailVerified) {
                updateUI(currentUser)
            }
        }
    }

    private fun signInUserWithGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        Log.d(TAG, "${result.resultCode} ${Activity.RESULT_OK}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        }
        Toast.makeText(this, result.data.toString() + " " + result.resultCode, Toast.LENGTH_SHORT).show()

        Log.d(TAG, result.data.toString())
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUIGS(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUIGS(null)
                }
            }
    }

    private fun updateUIGS(user:FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, ProfileUpdatePage::class.java)
            startActivity(intent)
        }
    }

    companion object{
        val TAG = "LoginActivity"
    }
}