package com.alexrotariu.finkeepy.data.repositories

import com.alexrotariu.finkeepy.data.datasources.RecordsDataSource
import com.alexrotariu.finkeepy.data.models.Record
import javax.inject.Inject

class RecordsRepository @Inject constructor(private val recordsDataSource: RecordsDataSource) {

    suspend fun getAllRecords(): List<Record?>? {
        return recordsDataSource.getAllRecords()
    }

    suspend fun getNetWorth(): Double? {
        return recordsDataSource.getMostRecentRecord()?.netWorth
    }
}