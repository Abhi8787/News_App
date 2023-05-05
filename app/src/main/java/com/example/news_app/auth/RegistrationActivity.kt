package com.example.news_app.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.news_app.R
import com.example.news_app.databinding.ActivityRegistrationBinding
import com.example.news_app.ui.NewsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var registerViewModel: RegisterViewModel
    private val mAuth = FirebaseAuth.getInstance()
    private val currentUser = mAuth.currentUser
    lateinit var profile : ImageView
    lateinit var imageUri : Uri
    lateinit var storage : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.lifecycleOwner = this

        binding.registerViewModel = registerViewModel


        registerViewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        registerViewModel.updateIntent.observe(this, Observer { message ->
            val mainActivityIntent = Intent(this, NewsActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        })


        profile = findViewById(R.id.profile)

        profile.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 10) {
                if (data != null) {
                    imageUri = data.data!!
                    profile.setImageURI(imageUri)
                }
            }
        }


    }
}