package com.example.myapplication.repository.remote

import android.net.Uri

data class ArticleEntity(
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?
) {
    fun getActualImageUrl(): Uri? {
        urlToImage?.apply {
            return Uri.parse(this)
        }
        return null
    }
}