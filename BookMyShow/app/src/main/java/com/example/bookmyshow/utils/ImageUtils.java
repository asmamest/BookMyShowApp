// ImageUtils.java
package com.example.bookmyshow.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bookmyshow.R;

public class ImageUtils {

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.event_1)
                    .error(R.drawable.event_1)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.event_1);
        }
    }
}