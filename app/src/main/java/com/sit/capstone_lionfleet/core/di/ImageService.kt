package com.sit.capstone_lionfleet.core.di

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageService
@Inject
constructor(@ApplicationContext private val context: Context) {
    fun loadImage(
        url: String?,
        imageView: ImageView
    ) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_vehicle_placeholder)
            .error(R.drawable.ic_vehicle_placeholder)
            .centerInside()
            .into(imageView)
    }

    fun loadImage(
        imageResId: Int,
        imageView: ImageView
    ) {
        Glide.with(context)
            .load(imageResId)
            .placeholder(R.drawable.ic_vehicle_placeholder)
            .error(R.drawable.ic_vehicle_placeholder)
            .centerInside()
            .into(imageView)
    }
}
