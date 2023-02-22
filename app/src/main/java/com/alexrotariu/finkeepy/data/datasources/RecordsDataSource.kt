package com.alexrotariu.finkeepy.data.datasources

import android.util.Log
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.models.Record.Companion.toRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecordsDataSource @Inject constructor(private val db: FirebaseFirestore) {

    private val TAG = "RECORDS_DATA_SOURCE"
    private val recordsCollection = db.collection("records")

    suspend fun getAllRecords() {
        recordsCollection.get()
            .addOnSuccessListener { collection ->
                if (collection != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${collection.documents}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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