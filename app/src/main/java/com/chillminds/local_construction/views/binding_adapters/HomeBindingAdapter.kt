package com.chillminds.local_construction.views.binding_adapters

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Logger

@BindingAdapter("animateVisibility")
fun setAnimatedVisibility(target: View, isVisible: Boolean) {

    TransitionManager.beginDelayedTransition(target.rootView as ViewGroup)
    target.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("count","setTotalPrice", requireAll = false)
fun setTotalPrice(target: TextView, count: String?, price:String?) {
    val totalPrice = ((count?:"1").toDoubleOrNull()?:1.0) * ((price?:"1").toDoubleOrNull()?:1.0)
    val text = "Total Price - $totalPrice"
    target.text = text
}

@BindingAdapter("animateSrc")
fun animateSrc(imageView: ImageView, resource: Drawable) {
    Logger.info("animateSrc", "${resource.isVisible}")
    Glide.with(imageView).asGif().load(R.drawable.construction).into(imageView)
}
