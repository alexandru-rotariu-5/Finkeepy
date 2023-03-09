package com.alexrotariu.finkeepy.utils

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun LocalDateTime.getShortMonth(): String {
    return DateTimeFormatter.ofPattern("MMM", Locale.getDefault()).format(this)
}

fun LocalDateTime.getShortMonthAndYear(): String {
    return DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault()).format(this)
}