package com.alexrotariu.finkeepy.data.repositories

import com.alexrotariu.finkeepy.data.datasources.RecordsDataSource
import javax.inject.Inject

class RecordsRepository @Inject constructor(private val recordsDataSource: RecordsDataSource) {

    suspend fun getAllRecords() {
        recordsDataSource.getAllRecords()
    }

    suspend fun getNetWorth(): Double? {
        return recordsDataSource.getMostRecentRecord()?.netWorth
    }
}