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
    private val disposable = CompositeDisposable()

    fun postArticles(list: List<ArticleEntity>?) {
        articles.postValue(list)
    }

    fun loadResults(name: String): LiveData<RequestResult<ApiEntity>> {
        return repository.fetchData(name).toLiveData()
    }

    fun onCardSwiped() {
        val tempArticle: ArrayList<ArticleEntity>? = (articles.value as? ArrayList<ArticleEntity>)
        tempArticle?.removeAt(0)
        articles.postValue(tempArticle)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}