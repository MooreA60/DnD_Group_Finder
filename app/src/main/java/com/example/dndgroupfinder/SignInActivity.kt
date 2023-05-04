package com.example.dndgroupfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.ktx.Firebase

//import androidx.activity.compose.setContent
//import org.w3c.dom.Text

//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.dndgroupfinder.ui.theme.DnDGroupFinderTheme

private const val TAG = "signin_activity"
class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)

        //Firebase authentication check
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goPostFeedActivity()
        }

        val signInBtn = findViewById<Button>(R.id.signInBtn)
        val enterEmail = findViewById<EditText>(R.id.enterEmail)
        val enterPassword =findViewById<EditText>(R.id.enterPassword)
        signInBtn.setOnClickListener{
            signInBtn.isEnabled = false
            val email = enterEmail.text.toString()
            val password = enterPassword.text.toString()

            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                signInBtn.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goPostFeedActivity()
                } else {
                    Log.e(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun goPostFeedActivity() {
        Log.i(TAG, "goPostFeedActivity")
        val intent = Intent(this, PostFeedActivity::class.java)
        startActivity(intent)
        finish()
    }
}


