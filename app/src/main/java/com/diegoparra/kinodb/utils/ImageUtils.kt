package com.diegoparra.kinodb.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import coil.transform.RoundedCornersTransformation
import com.diegoparra.kinodb.R

fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.loading_animation,
    @DrawableRes error: Int = R.drawable.ic_broken_image
) {
    this.load(url, builder = {
        placeholder(placeholder)
        error(error)
        crossfade(true)
    })
}