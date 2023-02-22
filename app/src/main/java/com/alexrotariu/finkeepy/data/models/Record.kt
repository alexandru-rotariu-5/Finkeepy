package com.alexrotariu.finkeepy.data.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Record(
    val recordId: String,
    val netWorth: Double,
    val income: Double,
    val timestamp: Date
) : Parcelable {

    companion object {
        fun DocumentSnapshot.toRecord(): Record? {
            return try {
                val netWorth = getDouble("netWorth")!!
                val income = getDouble("income")!!
                val timestamp = getTimestamp("timestamp")!!.toDate()
                Record(id, netWorth, income, timestamp)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting record", e)
                null
            }
        }

        private const val TAG = "Record"
    }
}