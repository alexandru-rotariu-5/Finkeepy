package com.alexrotariu.finkeepy.utils

import android.content.Context

class LayoutUtils {
    companion object {
        fun getDpToPxDensity(context: Context): Float {
            val displayMetrics = context.resources.displayMetrics
            return displayMetrics.density
        }

        fun getDpToPx(dp: Int, context: Context): Float {
            val density = getDpToPxDensity(context)
            return dp * density
        }
    }
}