package com.woleapp.netpos.qrgenerator.adapter

import android.view.View
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.woleapp.netpos.qrgenerator.BuildConfig

@BindingAdapter("determineWhenToShow")
fun CardView.determineWhenToShow(parameter: String) {
    visibility = if (BuildConfig.FLAVOR.contains("tallywallet")) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
