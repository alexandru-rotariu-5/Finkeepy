package com.alexrotariu.finkeepy.data.repositories

import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.network.RecordsDataSource
import javax.inject.Inject

class RecordsRepository @Inject constructor(private val recordsDataSource: RecordsDataSource) {

    suspend fun getAllRecords(): List<Record?>? {
        return recordsDataSource.getRecords(10_000)
    }
}