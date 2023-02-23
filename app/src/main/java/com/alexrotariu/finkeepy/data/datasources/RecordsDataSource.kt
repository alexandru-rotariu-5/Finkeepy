package com.alexrotariu.finkeepy.data.datasources

import android.util.Log
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.models.Record.Companion.toRecord
import com.alexrotariu.finkeepy.data.models.Record.Companion.toRecordList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecordsDataSource @Inject constructor(db: FirebaseFirestore) {

    private val TAG = "RECORDS_DATA_SOURCE"
    private val recordsCollection = db.collection("records")

    suspend fun getAllRecords(): List<Record?>? {
        return try {
            recordsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await().documents.toRecordList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting record", e)
            null
        }
    }

    suspend fun getMostRecentRecord(): Record? {
        return try {
            recordsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get().await().documents[0].toRecord()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting record", e)
            null
        }
    }
}