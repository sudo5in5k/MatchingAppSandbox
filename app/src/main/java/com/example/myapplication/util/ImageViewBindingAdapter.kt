package com.example.myapplication.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("bind:imageUrl")
fun ImageView.loadImage(url: String) {
    Glide.with(this.context).load(url).into(this)
}
