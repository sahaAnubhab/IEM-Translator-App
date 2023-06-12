package com.example.iemtranslatorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = Firebase.auth
        db = Firebase.firestore

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        //Normal Handler is deprecated , so we have to change the code little bit

        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({
            if(auth.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
            }else{
                if(auth.currentUser!!.isEmailVerified){
                    db.collection("users").document(auth.currentUser!!.uid).get()
                        .addOnCompleteListener {
                            val doc = it.result
                            if (doc.exists()) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this, ProfileUpdatePage::class.java))
                                finish()
                            }
                        }
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

        }, 2000) // 3000 is the delayed time in milliseconds.

    }
}