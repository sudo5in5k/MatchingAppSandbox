package com.example.myapplication.repository.remote

data class ApiEntity(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleEntity>?
)