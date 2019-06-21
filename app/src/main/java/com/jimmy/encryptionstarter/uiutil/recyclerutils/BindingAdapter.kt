package com.jimmy.encryptionstarter.uiutil.recyclerutils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter(value=["imageResource"])
fun ImageView.setImageResource(petImg: String ) {
    val resourceID = context.resources.getIdentifier(petImg,
        "drawable", context.packageName)
    setImageResource(resourceID)
}
