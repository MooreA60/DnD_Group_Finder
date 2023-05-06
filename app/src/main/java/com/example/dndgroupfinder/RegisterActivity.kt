package com.example.dndgroupfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.dndgroupfinder.ui.theme.DnDGroupFinderTheme
import com.google.firebase.auth.FirebaseAuth
//import com.google.rpc.context.AttributeContext.Auth

private const val TAG = "register_activity"

class RegisterActivity : ComponentActivity() {

    /// Instance of FirebaseAuth
    private lateinit var regAuth: FirebaseAuth

    val registerButton = findViewById<Button>(R.id.registerButton)
    val registerEmail = findViewById<EditText>(R.id.registerEmail)
    val registerPassword =findViewById<EditText>(R.id.registerPassword)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)



        regAuth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener{
            registerButton.isEnabled = false
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            // To check neither field is empty
            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // To check if the email entered is a valid email address
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Valid email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            // To check if the password has 6+ characters
            if (password.length < 6){
                Toast.makeText(this, "Password must be 6 characters minimum", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            registerUser(email, password)
        }


    }

    private fun registerUser(email: String, password: String) {
        regAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            registerButton.isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(this, "New user created!", Toast.LENGTH_SHORT).show()
                goPostFeedActivity()
            } else {
                Log.e(TAG, "Register user failed", task.exception)
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
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

