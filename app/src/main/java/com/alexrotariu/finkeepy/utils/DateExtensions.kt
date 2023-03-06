package com.alexrotariu.finkeepy.utils

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}
