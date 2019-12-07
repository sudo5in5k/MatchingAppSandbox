package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ItemBinding
import com.example.myapplication.repository.remote.ArticleEntity
import com.example.myapplication.util.RequestResult
import com.example.myapplication.util.ViewModelFactory
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    var likeCount = 0
    var nopeCount = 0

    //private var data: ArrayList<ArticleEntity>? = null
    private val disposable = CompositeDisposable()

    private lateinit var viewModel: MainViewModel
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        activityMainBinding.also {
            it.activity = this@MainActivity
            it.viewModel = viewModel
        }

        val cardAdapter = CardAdapter(viewModel.articles.value)
        val cardsCount = cardAdapter.count
        var count = cardAdapter.count

        val countButton = findViewById<Button>(R.id.count_button)
        countButton.text = "$count / $cardsCount"

        activityMainBinding.fling.also {
            it.adapter = cardAdapter
            it.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
                override fun removeFirstObjectInAdapter() {
                    return
                }

                override fun onLeftCardExit(article: Any?) {
                    viewModel.onCardSwiped()
                    count--
                    likeCount++
                    cardAdapter.notifyDataSetChanged()
                    countButton.text = "$count / $cardsCount"
                    if (count == 0) {
                        showBackView()
                    }
                }

                override fun onRightCardExit(article: Any?) {
                    viewModel.onCardSwiped()
                    count--
                    nopeCount++
                    cardAdapter.notifyDataSetChanged()
                    countButton.text = "$count / $cardsCount"
                    if (count == 0) {
                        showBackView()
                    }
                }

                override fun onAdapterAboutToEmpty(p0: Int) {
                    return
                }

                override fun onScroll(p0: Float) {
                    it.selectedView.apply {
                        findViewById<View>(R.id.item_swipe_left_indicator).alpha =
                            if (p0 < 0) -p0 else 0.0F
                        findViewById<View>(R.id.item_swipe_right_indicator).alpha =
                            if (p0 > 0) p0 else 0.0F
                    }
                }

            })

            it.setOnItemClickListener { _, _ ->
                cardAdapter.notifyDataSetChanged()
            }
        }

        viewModel.loadResults(NAME).observe(this, Observer { result ->
            when (result) {
                is RequestResult.Success -> {
                    val resultData = result.data
                    if (resultData.status == "ok") {
                        //TODO 冗長
                        cardAdapter.setAllItem(resultData.articles)
                        viewModel.postArticles(resultData.articles)
                    }
                }
                is RequestResult.Failure -> {
                    Log.d("ushi", "${result.t}")
                }
            }
        })

        viewModel.articles.observe(this, Observer {

            Log.d("ushi", "articleObserve: $it")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun showBackView() {
        findViewById<View>(R.id.finish_matching_text).visibility = View.VISIBLE
        findViewById<View>(R.id.grid).visibility = View.VISIBLE
        findViewById<TextView>(R.id.likeCount).text = "$likeCount"
        findViewById<TextView>(R.id.nopeCount).text = "$nopeCount"
    }

    class CardAdapter(
        private var adapterArticles: List<ArticleEntity>?
    ) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val binding = if (convertView == null) {
                ItemBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                ).apply {
                    root.tag = this
                }
            } else {
                convertView.tag as ItemBinding
            }
            binding.article = getItem(position)
            binding.imageUrl = getItem(position)?.urlToImage
            return binding.root
        }

        fun setAllItem(articles: List<ArticleEntity>?) {
            adapterArticles = articles
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): ArticleEntity? {
            return adapterArticles?.get(position)
        }

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount(): Int {
            return adapterArticles?.count() ?: 0
        }

    }

    companion object {
        const val NAME = "タピオカ"
    }
}
