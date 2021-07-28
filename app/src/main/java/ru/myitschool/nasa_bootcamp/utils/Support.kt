package ru.myitschool.nasa_bootcamp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.myitschool.nasa_bootcamp.R

public fun loadImage(context: Context, url: String?, view : ImageView) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}

