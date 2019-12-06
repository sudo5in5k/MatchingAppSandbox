package com.example.myapplication.repository

import com.example.myapplication.repository.remote.ApiEntity
import com.example.myapplication.repository.remote.NewsApi
import io.reactivex.Flowable

interface Repository {
    val api: NewsApi
    fun fetchData(name: String): Flowable<ApiEntity>
}