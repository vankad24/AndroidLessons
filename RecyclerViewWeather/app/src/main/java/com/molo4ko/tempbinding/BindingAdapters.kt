package com.molo4ko.tempbinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageRes")
    fun setImageRes(view: ImageView, resId: Int) {
        view.setImageResource(resId)
    }
}