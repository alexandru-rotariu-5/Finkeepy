package com.alexrotariu.finkeepy.utils

import android.content.Context

class LayoutUtils {
    companion object {
        fun getDpToPx(context: Context): Float {
            val displayMetrics = context.resources.displayMetrics
            return displayMetrics.density
        }
    }
}