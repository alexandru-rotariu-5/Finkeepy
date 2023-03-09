package com.alexrotariu.finkeepy.utils

fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}