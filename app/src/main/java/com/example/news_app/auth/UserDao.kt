package com.example.news_app.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    // data access object.
    // Userdao work will be user ke data ko user ki entry mein daalna.
    // user data will get from firebase.

     private val user = FirebaseAuth.getInstance().currentUser
     private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
     private val myRef: DatabaseReference = database.reference
     private val userRef: DatabaseReference = myRef.child("users").child(""+user?.uid)


     fun addUser(user : User?)
     {
          user?.let {
               GlobalScope.launch(Dispatchers.IO) {
                  userRef.setValue(user)
               }
          }
     }
}