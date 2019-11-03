package com.example.myapplication.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.repository.SampleData
import com.example.myapplication.repository.SampleDataRepository
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    var likeCount = 0
    var nopeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = SampleDataRepository().getDataList()
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
                    return Unit
                }

                override fun onLeftCardExit(p0: Any?) {
                    data.removeAt(0)
                    count--
                    likeCount++
                    cardAdapter.notifyDataSetChanged()
                    countButton.text = "$count / $cardsCount"
                    if (count == 0) {
                        showBackView()
                    }
                }

                override fun onRightCardExit(p0: Any?) {
                    data.removeAt(0)
                    count--
                    nopeCount++
                    cardAdapter.notifyDataSetChanged()
                    countButton.text = "$count / $cardsCount"
                    if (count == 0) {
                        showBackView()
                    }
                }

                override fun onAdapterAboutToEmpty(p0: Int) {
                    return Unit
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

    fun showBackView() {
        findViewById<View>(R.id.finish_matching_text).visibility = View.VISIBLE
        findViewById<View>(R.id.grid).visibility = View.VISIBLE
        findViewById<TextView>(R.id.likeCount).text = "$likeCount"
        findViewById<TextView>(R.id.nopeCount).text = "$nopeCount"
    }

    class CardViewHolder(val title: TextView, val descriptor: TextView, val cardImage: ImageView)

    class CardAdapter(private val arrayList: ArrayList<SampleData>, private val context: Context) :
        BaseAdapter() {
        private lateinit var viewHolder: CardViewHolder
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var rowView = convertView
            if (rowView == null) {
                val inflater = LayoutInflater.from(context)
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
            viewHolder.title.text = arrayList[position].title
            viewHolder.descriptor.text = arrayList[position].description
            Glide.with(context).load(arrayList[position].imagePath).into(viewHolder.cardImage)
            return rowView!!
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount(): Int {
            return arrayList.count()
        }

    }
}
