package com.example.news_app.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.news_app.R
import com.example.news_app.models.Article
import com.example.news_app.ui.NewsActivity
import com.example.news_app.ui.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    lateinit var webView : WebView
    lateinit var fab : FloatingActionButton
    val TAG = "ArticleFragment"

//    val args : ArticleFragmentArgs by navArgs()
//    val args : ArticleFragmentArgs by navArgs()

    val args = this.arguments

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        webView = view.findViewById(R.id.webView)
        fab = view.findViewById(R.id.fab)

        val inputData = arguments?.getString("article")

//        if(inputData != null){
//             val article = gson.fromJson(json, Article::class.java)
//        }
        val article = Gson().fromJson(inputData , Article::class.java)
        Log.e(TAG , ""+inputData)


//        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view , "Article Saved Successfully" , Snackbar.LENGTH_SHORT).show()
        }

    }
}