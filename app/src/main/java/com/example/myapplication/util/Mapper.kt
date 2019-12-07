package com.example.myapplication.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.Flowable
import org.reactivestreams.Publisher

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>

fun <T> Flowable<T>.toResult(): Flowable<RequestResult<T>> {
    return compose { item ->
        item.map { RequestResult.success(it) }
            .onErrorReturn { e -> RequestResult.failure(e) }
    }
}