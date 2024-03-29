package com.example.myapplication.repository

import com.example.myapplication.repository.remote.ApiEntity
import com.example.myapplication.repository.remote.ApiService
import com.example.myapplication.repository.remote.NewsApi
import com.example.myapplication.util.RequestResult
import com.example.myapplication.util.toResult
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class NewsRepository : Repository {
    override val api: NewsApi
        get() = ApiService()

    override fun fetchData(name: String): Flowable<RequestResult<ApiEntity>> {
        return api.query(name, "3b007aa115c24b6e8f05844c7c33c621", "popularity", 10)
            .subscribeOn(Schedulers.io()).toResult()
    }
}