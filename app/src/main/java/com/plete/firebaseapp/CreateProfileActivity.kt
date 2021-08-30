package com.plete.firebaseapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.authsetapp.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var createButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var database: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth:FirebaseAuth

    private var imageUri: Uri? = null
    private var uid: String? = null

    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        database = Firebase.database
        firestore = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth

        profile = Profile()

        imageView = findViewById(R.id.profileImageView)
        nameEditText = findViewById(R.id.etCreateName)
        bioEditText = findViewById(R.id.etCreateBio)
        emailEditText = findViewById(R.id.etCreateEmail)
        createButton = findViewById(R.id.btCreate)
        progressBar = findViewById(R.id.createProgressBar)

        val currentUser = auth.currentUser
        currentUser?.let {
            uid = it.uid
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            try {
                if (it?.resultCode == Activity.RESULT_OK){
                    it.data?.let {
                        imageUri = it.data
                        Glide.with(this).load(imageUri).into(imageView)
                    }
                }
            } catch (e: Exception){
                Toast.makeText(this, "eroor ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        imageView.setOnClickListener {
            val intent = Intent().apply {
                setType("image/*")
                setAction(Intent.ACTION_GET_CONTENT)
            }
            resultLauncher.launch(intent)
        }

        createButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val bio = bioEditText.text.toString()
            val email = emailEditText.text.toString()

            if (name.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty()){
                progressBar.visibility = View.VISIBLE
                imageUri?.let {
                    val contentResolver: ContentResolver = getContentResolver()
                    val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it))

                    val reference = storage.getReference("profile_images").child(System.currentTimeMillis().toString() + ".${fileExtension}")
                    val uploadTask = reference.putFile(it)
                    uploadTask.continueWith{
                        if (!it.isSuccessful){
                            throw it.exception!!.cause!!
                        }

                        reference.downloadUrl
                    }.addOnCompleteListener {
                        if (it.isSuccessful){
                            it.result?.let{
                                val downloadUri =  it
                                val profileMap = HashMap<String, String>()
                                profileMap.put("name", name)
                                profileMap.put("bio", bio)
                                profileMap.put("email", email)
                                profileMap.put("url", downloadUri.toString())
                                profileMap.put("uid", uid!!)

                                profile.name = name
                                profile.url = downloadUri.toString()
                                profile.uid = uid

                                uid?.let {
                                    database.getReference("users").child(it).setValue(profile)
                                    firestore.collection("users").document(it).set(profileMap).addOnSuccessListener {
                                        progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(this, "profile created", Toast.LENGTH_SHORT).show()

                                        val handler = Handler(Looper.getMainLooper())
                                        handler.postDelayed({
                                            val intent = Intent(this, ProfileActivity::class.java)
                                            startActivity(intent)
                                        }, 2000)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "plis fill the blnk", Toast.LENGTH_SHORT).show()
            }
        }
    }
}