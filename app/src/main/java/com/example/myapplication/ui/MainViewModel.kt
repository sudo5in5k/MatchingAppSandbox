package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.Repository
import com.example.myapplication.repository.remote.ApiEntity
import com.example.myapplication.repository.remote.ArticleEntity
import com.example.myapplication.util.RequestResult
import com.example.myapplication.util.toLiveData
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(private val repository: Repository) : ViewModel() {

    val articles = MutableLiveData<List<ArticleEntity>>()
    val likeCount = MutableLiveData(0)
    val nopeCount = MutableLiveData(0)
    private val initSize = MutableLiveData(0)
    val countText = MutableLiveData("- / -")
    val contentsEnd = MutableLiveData(false) // 全記事既読状態か判定

    private val disposable = CompositeDisposable()

    fun postArticles(list: List<ArticleEntity>?) {
        articles.postValue(list)
    }

    fun initCount(size: Int) {
        initSize.postValue(size)
        countText.postValue("$size / $size")
    }

    fun loadResults(name: String): LiveData<RequestResult<ApiEntity>> {
        return repository.fetchData(name).toLiveData()
    }

    private fun onCardSwiped() {
        val tempArticle: ArrayList<ArticleEntity>? = (articles.value as? ArrayList<ArticleEntity>)
        tempArticle?.removeAt(0)
        articles.postValue(tempArticle)
        countText.postValue("${tempArticle?.size ?: '-'} / ${initSize.value ?: '-'}")

        if (tempArticle?.size == 0) {
            contentsEnd.postValue(true)
        }
    }

    fun onLeftSwiped() {
        var likes = likeCount.value ?: 0
        likes++
        likeCount.postValue(likes)
        onCardSwiped()
    }

    fun onRightSwiped() {
        var nopes = nopeCount.value ?: 0
        nopes++
        nopeCount.postValue(nopes)
        onCardSwiped()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}