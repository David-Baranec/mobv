
package com.example.cvicenie2

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, file: File?) {
    if (file != null) {
        Glide.with(view.context)
            .load(file)
            .into(view)
    } else {
        // You can set a placeholder image or handle the case when the file is null
        view.setImageDrawable(null)
    }
}