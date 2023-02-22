package com.alexrotariu.finkeepy.utils

import java.text.DecimalFormat

fun Double.toFormattedString(): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(this)
}