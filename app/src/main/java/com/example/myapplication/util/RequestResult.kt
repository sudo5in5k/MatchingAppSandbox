package com.example.myapplication.util

sealed class RequestResult<T> {
    data class Success<T>(val data: T) : RequestResult<T>()
    data class Failure<T>(val t: Throwable) : RequestResult<T>()

    companion object {
        fun <T> success(data: T): RequestResult<T> = Success(data)
        fun <T> failure(e: Throwable): RequestResult<T> = Failure(e)
    }
}