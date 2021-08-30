package com.plete.firebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var checkBox: CheckBox
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.etRegEmail)
        passwordEditText = findViewById(R.id.etRegPass)
        confirmPasswordEditText = findViewById(R.id.etConfirmPass)
        registerButton = findViewById(R.id.btRegReg)
        loginButton = findViewById(R.id.btRegLogin)
        checkBox = findViewById(R.id.regisCheckBox)
        progressBar = findViewById(R.id.regProgressBar)
        auth = Firebase.auth

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() || password.isNotEmpty() || confirmPassword.isNotEmpty()) {
                if (password.equals(confirmPassword)){
                    progressBar.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent (this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val error = it.exception
                            error?.let {
                                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        progressBar.visibility = View.INVISIBLE
                    }
                } else {
                    progressBar.visibility =View.INVISIBLE
                    Toast.makeText(this, "Password and confirm password not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}