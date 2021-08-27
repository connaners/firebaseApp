package com.plete.firebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.etRegEmail)
        passwordEditText = findViewById(R.id.etRegPass)
        registerButton = findViewById(R.id.btRegReg)
        auth = Firebase.auth

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val pass = passwordEditText.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                auth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val error = it.exception
                            Toast.makeText(this, "error: {$error}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }
}