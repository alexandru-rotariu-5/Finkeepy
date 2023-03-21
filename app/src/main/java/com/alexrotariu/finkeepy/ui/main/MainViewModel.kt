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

    fun getNetWorth(): Double {
        return records.value?.get(0)?.netWorth ?: 0.0
    }

    fun getLastMonthCashflow(): Double {
        return records.value?.get(1)?.netWorth?.let { records.value?.get(0)?.getCashflow(it) }
            ?: 0.0
    }

    fun getChartEntries(valueType: ValueType): List<Entry> {
        val entries: MutableList<Entry> = mutableListOf()
        val reversedRecords = records.value?.reversed()

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
}