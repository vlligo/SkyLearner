package com.example.astroguessr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = Firebase.auth

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener { logout() }
        findViewById<MaterialButton>(R.id.btnChangePassword).setOnClickListener { changePassword() }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finishAffinity()  // Close all activities
    }

    private fun changePassword() {
        val user = auth.currentUser
        val email = user?.email ?: run {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}