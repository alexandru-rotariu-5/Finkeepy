package com.alexrotariu.finkeepy.data.models

import android.os.Parcelable
import android.util.Log
import com.alexrotariu.finkeepy.utils.toLocalDateTime
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Parcelize
data class Record(
    val recordId: String,
    val netWorth: Double,
    val income: Double,
    val timestamp: LocalDateTime
) : Parcelable {

    fun getExpense(previousNetWorth: Double): Double {
        return (previousNetWorth + income) - netWorth
    }

    fun getCashflow(previousNetWorth: Double): Double {
        return netWorth - previousNetWorth
    }

    companion object {
        fun DocumentSnapshot.toRecord(): Record? {
            return try {
                val netWorth = getDouble("netWorth")!!
                val income = getDouble("income")!!
                val timestamp = getTimestamp("timestamp")!!.toLocalDateTime()
                Record(id, netWorth, income, timestamp)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting record", e)
                null
            }
        }

        fun List<DocumentSnapshot>.toRecordList(): List<Record?>? {
            return try {
                this.map { it.toRecord() }
            } catch (e: Exception) {
                Log.e(TAG, "Error converting record list", e)
                null
            }
        }

        private const val TAG = "Record"
    }
}