package com.example.news_app.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news_app.NewsApplication
import com.example.news_app.auth.User
import com.example.news_app.models.Article
import com.example.news_app.models.NewsResponse
import com.example.news_app.repository.NewsRepository
import com.example.news_app.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app : Application ,
    val newsRepository : NewsRepository) : AndroidViewModel(app) {

   val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
   var breakingNewsPage = 1
   var breakingNewsResponse : NewsResponse?= null


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse?= null


    init {
        getBreakingNews("in")
    }

   fun getBreakingNews(countryCode:String) = viewModelScope.launch {
//       breakingNews.postValue(Resource.Loading())
//       val response = newsRepository.getBreakingNews(countryCode , breakingNewsPage)
//       breakingNews.postValue(handleBreakingNewsResponse(response))

       safeBreakingNewsCall(countryCode)
   }


    fun searchNews(searchQuery : String) = viewModelScope.launch {
//        searchNews.postValue(Resource.Loading())
//        val response = newsRepository.searchNews(searchQuery , searchNewsPage)
//        searchNews.postValue(handleSearchNewsResponse(response))

        safeSearchNewsCall(searchQuery)
    }


   private fun handleBreakingNewsResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {

       if(response.isSuccessful){
           response.body()?.let {resultResponse->
               breakingNewsPage++
               if(breakingNewsResponse == null){
                   breakingNewsResponse = resultResponse
               }
               else{
                   val oldArticles = breakingNewsResponse?.articles
                   val newArticles = resultResponse.articles
                   oldArticles?.addAll(newArticles)
               }
               return Resource.Success(breakingNewsResponse ?:  resultResponse)
           }
       }

       return  Resource.Error(response.message())
   }


    private fun handleSearchNewsResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {

        if(response.isSuccessful){
            response.body()?.let {resultResponse->
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }
                else{
                    val oldArticles = searchNewsResponse?.articles
                    val newsArticles = resultResponse.articles
                    oldArticles?.addAll(newsArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }

        return  Resource.Error(response.message())
    }


    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }



    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode , breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }   else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){

            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery , searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }   else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){

            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }




    private fun hasInternetConnection() : Boolean{
       val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE
       ) as ConnectivityManager

    //  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
          val activeNetwork = connectivityManager.activeNetwork ?: return false
          val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

          return when{
              capabilities.hasTransport(TRANSPORT_WIFI) -> true
              capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
              capabilities.hasTransport(TRANSPORT_ETHERNET) -> true

              else -> false
          }
     // }


//      else{
        // for sdk version <= 23 can use this inbuild functions but at later version it is been deprecated.
//        connectivityManager.activeNetworkInfo?.run {
//            return  when(type){
//                TYPE_WIFI -> true
//                TYPE_MOBILE -> true
//                TYPE_ETHERNET -> true
//
//                else -> false
//            }
//        }
//      }
//
//        return false

    }








//    fun  getUser() : User?
//    {
//        val database = FirebaseDatabase.getInstance().getReference("users")
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        var user = User();
//
//        userId?.let { uid ->
//            database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    // Handle the data here
//                    var userData = dataSnapshot.getValue(User::class.java)
//
//                    if (userData != null) {
//                        Log.d("Abhi" , userData.state + " " + userData.displayName)
//                    }
//                    if (userData != null) {
//                        user = userData
//                    }
//                    // Do something with userData
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle the error here
//                }
//            })
//        }
//
//        return user
//    }



    fun getUser(callback: (User?) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle the data here
                    val userData = dataSnapshot.getValue(User::class.java)
                    callback(userData)
                    // Do something with userData
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error here
                    callback(null)
                }
            })
        }
    }



















}