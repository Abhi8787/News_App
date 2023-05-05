package com.example.news_app.auth

import androidx.lifecycle.MutableLiveData

data class User(
    val uid: String = "",
    val imageUrl: String = "",
    val displayName: String? = "",
    val email: String = "",
    val gender: String = "",
    val phone: String = "",
    val city: String = "",
    val pinCode: String = "",
    val state: String = "")