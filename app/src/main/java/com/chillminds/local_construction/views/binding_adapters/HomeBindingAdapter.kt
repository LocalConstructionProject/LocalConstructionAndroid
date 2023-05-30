package com.chillminds.local_construction.views.binding_adapters

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager

@BindingAdapter("animateVisibility")
fun setAnimatedVisibility(target: View, isVisible: Boolean) {

    TransitionManager.beginDelayedTransition(target.rootView as ViewGroup)
    target.visibility = if (isVisible) View.VISIBLE else View.GONE
}