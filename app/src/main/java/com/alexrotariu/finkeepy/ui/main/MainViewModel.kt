package com.alexrotariu.finkeepy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.repositories.RecordsRepository
import com.alexrotariu.finkeepy.ui.models.ValueType
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class MainViewModel @Inject constructor(private val recordsRepository: RecordsRepository) :
    ViewModel() {

    private val _records = MutableLiveData<List<Record?>?>()
    val records: LiveData<List<Record?>?> = _records

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getRecords()
    }

    fun getRecords(callback: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = recordsRepository.getAllRecords()
            response?.let { _records.value = response }
            callback()
            _isLoading.value = false
        }
    }

    private fun getRecordsByTimeRange(timeRange: Pair<Float, Float>): List<Record?>? {
        return records.value?.reversed()?.subList(timeRange.first.toInt(), timeRange.second.toInt())
    }

    fun getNetWorth(
        timeRange: Pair<Float, Float> = Pair(
            0f,
            records.value?.size?.toFloat() ?: 0f
        )
    ): Double {
        return getRecordsByTimeRange(timeRange)?.last()?.netWorth ?: 0.0
    }

    fun getTotalIncome(timeRange: Pair<Float, Float>): Double {
        return getRecordsByTimeRange(timeRange)?.sumOf { it?.income ?: 0.0 } ?: 0.0
    }

    fun getTotalExpense(timeRange: Pair<Float, Float>): Double {
        val adaptedTimeRange = Pair(
            if (timeRange.first == 0f) timeRange.first else timeRange.first - 1f,
            timeRange.second
        )
        val recordsList = getRecordsByTimeRange(adaptedTimeRange)!!
        val adaptedRecordsListIndices =
            if (timeRange.first == 0f) recordsList.indices else recordsList.indices.toList()
                .subList(1, recordsList.size)
        var totalExpense = 0.0
        for (i in adaptedRecordsListIndices) {
            val previousNetWorth = recordsList.getOrNull(i - 1)?.netWorth ?: 0.0
            totalExpense += recordsList[i]?.getExpense(previousNetWorth)
                ?: 0.0
        }
        return totalExpense
    }

    fun getAverageIncome(timeRange: Pair<Float, Float>): Double {
        return getTotalIncome(timeRange) / getRecordsByTimeRange(timeRange)!!.size
    }

    fun getAverageExpense(timeRange: Pair<Float, Float>): Double {
        return getTotalExpense(timeRange) / getRecordsByTimeRange(timeRange)!!.size
    }

    fun getAverageCashflow(timeRange: Pair<Float, Float>): Double {
        return getAverageIncome(timeRange) - getAverageExpense(timeRange)
    }

    fun getLastMonthCashflow(): Double {
        return records.value?.get(1)?.netWorth?.let { records.value?.get(0)?.getCashflow(it) }
            ?: 0.0
    }

    fun getChartEntries(
        valueType: ValueType,
        range: Pair<Float, Float> = Pair(0f, records.value?.size?.toFloat() ?: 0f)
    ): List<Entry> {
        val entries: MutableList<Entry> = mutableListOf()
        val reversedRecords =
            records.value?.reversed()?.subList(range.first.toInt(), range.second.toInt())

        reversedRecords?.forEachIndexed { index, record ->
            val previousNetWorth = reversedRecords.getOrNull(index - 1)?.netWorth ?: 0.0
            record?.let {
                val xValue = index.toFloat()
                val yValue = when (valueType) {
                    ValueType.NET_WORTH -> record.netWorth.toFloat()
                    ValueType.INCOME -> record.income.toFloat()
                    ValueType.EXPENSE -> record.getExpense(previousNetWorth).toFloat()
                    ValueType.CASHFLOW -> record.getCashflow(previousNetWorth).toFloat()
                }
                entries.add(Entry(xValue, yValue))
            }
        }
        return entries
    }

    fun getTimeRange(): List<LocalDateTime?> {
        return records.value?.reversed()?.map { it?.timestamp } ?: listOf()
    }
}