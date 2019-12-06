package com.example.myapplication.repository.remote

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @see: https://supporterzcolab.com/about/api/
 */
interface NewsApi {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
    }

    @GET("everything")
    fun query(
        @Query("q") q: String,
        @Query("apiKey") key: String,
        @Query("sortBy") sortBy: String,
        @Query("pageSize") pageSize: Int
    ): Flowable<ApiEntity>

}