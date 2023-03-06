package com.alexrotariu.finkeepy.utils

import java.text.DecimalFormat

fun <T : Number> T.format(): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(this)
}

fun Double.split(): Pair<Int, Int> {
    val whole = this.toInt()
    val decimal = ((this - whole) * 100).toInt()
    return Pair(whole, decimal)
}

fun String.formatDecimalString(): String {
    return if (this == "0") {
        "00"
    } else {
        this
    }
}