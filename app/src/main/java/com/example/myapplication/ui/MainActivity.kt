package com.example.myapplication.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.repository.remote.ArticleEntity
import com.example.myapplication.util.RequestResult
import com.example.myapplication.util.ViewModelFactory
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var likeCount = 0
    var nopeCount = 0

    private var data: ArrayList<ArticleEntity>? = null
    private val disposable = CompositeDisposable()

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        viewModel.loadResults(NAME).observe(this, Observer { result ->
            when (result) {
                is RequestResult.Success -> {
                    val resultData = result.data
                    if (resultData.status == "ok") {
                        viewModel.postArticles(resultData.articles)
                        Log.d("ushi", resultData.articles?.get(0)?.description ?: "null?")
                    }
                }
                is RequestResult.Failure -> {
                    Log.d("ushi", "${result.t}")
                }
            }
        })
        val cardAdapter = CardAdapter(data, this)
        val cardsCount = cardAdapter.count
        var count = cardAdapter.count

        val countButton = findViewById<Button>(R.id.count_button)
        countButton.text = "$count / $cardsCount"

        val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.fling)
        fling.apply {
            adapter = cardAdapter
            setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
                override fun removeFirstObjectInAdapter() {
                    return
                }

                override fun onLeftCardExit(p0: Any?) {
                    data?.removeAt(0)
                    count--
                    likeCount++
                    cardAdapter.notifyDataSetChanged()
                    countButton.text = "$count / $cardsCount"
                    if (count == 0) {
                        showBackView()
                    }
                }

                override fun onRightCardExit(p0: Any?) {
                    data?.removeAt(0)
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
                    flingContainer.selectedView.apply {
                        findViewById<View>(R.id.item_swipe_left_indicator).alpha =
                            if (p0 < 0) -p0 else 0.0F
                        findViewById<View>(R.id.item_swipe_right_indicator).alpha =
                            if (p0 > 0) p0 else 0.0F
                    }
                }

            })

            setOnItemClickListener { _, _ ->
                cardAdapter.notifyDataSetChanged()
            }
        }
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

    class CardViewHolder(val title: TextView, val descriptor: TextView, val cardImage: ImageView)

    class CardAdapter(
        private val arrayList: ArrayList<ArticleEntity>?,
        private val context: Context
    ) :
        BaseAdapter() {
        private lateinit var viewHolder: CardViewHolder
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var rowView = convertView
            if (rowView == null) {
                val inflater = LayoutInflater.from(parent?.context)
                rowView = inflater.inflate(R.layout.item, parent, false)
                viewHolder = CardViewHolder(
                    rowView.findViewById(R.id.title),
                    rowView.findViewById(R.id.description),
                    rowView.findViewById(R.id.cardImage)
                )
                rowView.tag = viewHolder
            } else {
                viewHolder = rowView.tag as CardViewHolder
            }
            viewHolder.title.text = arrayList?.get(position)?.title
            viewHolder.descriptor.text = arrayList?.get(position)?.description
            Glide.with(context).load(arrayList?.get(position)?.getActualImageUrl())
                .into(viewHolder.cardImage)
            return rowView!!
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount(): Int {
            return arrayList?.count() ?: 0
        }

    }

    companion object {
        const val NAME = "tapioka"
    }
}
