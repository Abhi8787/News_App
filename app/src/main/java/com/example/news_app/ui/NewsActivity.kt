package com.example.news_app.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.news_app.R
import com.example.news_app.auth.RegistrationActivity
import com.example.news_app.auth.SignIn
import com.example.news_app.auth.User
import com.example.news_app.db.ArticleDatabase
import com.example.news_app.repository.NewsRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class NewsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
     lateinit var viewModel: NewsViewModel
     lateinit var toolbarText : TextView
     lateinit var logOut : ImageView
     lateinit var profile : ImageView
     lateinit var navigationView : NavigationView
     lateinit var edit : ImageView
     lateinit var drawerLayout : DrawerLayout


     lateinit var nameDisplayTv : TextView
     lateinit var emailDisplayTv : TextView
     lateinit var phoneDisplayTV : TextView
     lateinit var cityDisplayTv : TextView
     lateinit var pinCodeDisplayTV : TextView
     lateinit var stateDisplayTV : TextView
     lateinit var profileNameMainDisplay : TextView
     lateinit var ProfileAvtarImageView : ImageView

//     lateinit var dialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        bottomNavigationView = findViewById(R.id.bottomNavigation)
        toolbarText =  findViewById(R.id.ToolbarText)
        logOut = findViewById(R.id.logout)
        profile = findViewById(R.id.profile)

        drawerLayout = findViewById(R.id.drawer)


        navigationView = findViewById(R.id.drawer_navigation)
        val headerView = navigationView.getHeaderView(0)
        edit = headerView.findViewById(R.id.edit)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        bottomNavigationView.setupWithNavController((navController))

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application ,newsRepository)
        viewModel = ViewModelProvider(this , viewModelProviderFactory).get(NewsViewModel::class.java)


//         dialog = Dialog(this@NewsActivity, R.style.Dialogue)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.breakingNewsFragment -> {
                    toolbarText.text = "Breaking News"
                }
                R.id.savedNewsFragment -> {
                    toolbarText.text = "Saved News"
                }
                R.id.searchNewsFragment -> {
                    toolbarText.text = "Searh News"
                }
                // add cases for other destinations as needed
            }
        }


        logOut.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@NewsActivity, R.style.Dialogue)
            dialog.setContentView(R.layout.dialog_layout)
            dialog.show()
            val yesbtn: TextView
            val nobtn: TextView
            yesbtn = dialog.findViewById(R.id.yes)
            nobtn = dialog.findViewById(R.id.no)
            yesbtn.setOnClickListener {
//                val sharedPreferences =
//                    getSharedPreferences("Data", MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.clear()
//                editor.commit()
                FirebaseAuth.getInstance().signOut()
                GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                startActivity(Intent(this@NewsActivity, SignIn::class.java))
                finish()
            }
            nobtn.setOnClickListener { dialog.dismiss() }
        })




        edit.setOnClickListener {
            startActivity(Intent(this@NewsActivity, RegistrationActivity::class.java))
            finish()
        }

        profile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }



        nameDisplayTv = headerView.findViewById(R.id.nameDisplayTv)
        emailDisplayTv = headerView.findViewById(R.id.emailDisplayTv)
        phoneDisplayTV = headerView.findViewById(R.id.phoneDisplayTV)
        cityDisplayTv = headerView.findViewById(R.id.cityDisplayTv)
        pinCodeDisplayTV = headerView.findViewById(R.id.pinCodeDisplayTV)
        stateDisplayTV = headerView.findViewById(R.id.stateDisplayTV)
        profileNameMainDisplay = headerView.findViewById(R.id.profileNameMainDisplay)
        ProfileAvtarImageView = headerView.findViewById(R.id.ProfileAvtarImageView)

        viewModel.getUser { userData ->
                // Handle the user data here
                if (userData != null) {

                    if(userData.imageUrl != null){
                        Glide.with(this).load(userData.imageUrl).into(ProfileAvtarImageView)
                    }
                    nameDisplayTv.setText(userData.displayName.toString())
                    emailDisplayTv.setText(userData.email.toString())
                    phoneDisplayTV.setText(userData.phone.toString())
                    cityDisplayTv.setText(userData.city.toString())
                    pinCodeDisplayTV.setText(userData.pinCode.toString())
                    stateDisplayTV.setText(userData.state.toString())
                    profileNameMainDisplay.setText(userData.displayName.toString())

                } else {
//                    println("Failed to fetch user data")
                }
            }




    }
}