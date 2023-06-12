package com.example.iemtranslatorapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.iemtranslatorapp.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        replaceFragment(HomeFragment())

        binding.bottomNavView.background = null
        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile -> replaceFragment(HistoryFragment())
                R.id.home -> replaceFragment(HomeFragment())
                R.id.settings -> replaceFragment(SettingsFragment())

            }
            return@setOnNavigationItemSelectedListener false
        }

        if (checkPermission()) {

        } else {
            requestPermission();
//        binding.navView.setNavigationItemSelectedListener(this)
//        binding.navView.bringToFront()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }
    
    private val PERMISSION_REQUEST_CODE = 200

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
//
//                // main logic
//            } else {
//                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        showMessageOKCancel("You need to allow access permissions",
//                            DialogInterface.OnClickListener { dialog, which ->
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                    requestPermission()
//                                }
//                            })
//                    }
//                }
//            }
//        }
//
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Please grant camera permission to use the QR Scanner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.sign_out -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }

            R.id.edit_profile ->{
                startActivity(Intent(this, ProfileActivity::class.java))
            }

        }
        return false
    }


}