package com.alexrotariu.finkeepy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.repositories.RecordsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val recordsRepository: RecordsRepository) :
    ViewModel() {

    private val _records = MutableLiveData<List<Record?>?>()
    val records: LiveData<List<Record?>?> = _records

    init {
        getRecords()
    }

    private fun getRecords() {
        viewModelScope.launch {
            val response = recordsRepository.getAllRecords()
            if (response != null) {
                _records.value = response
            }
        }
    }

    fun getNetWorth(): Double {
        return records.value?.get(0)?.netWorth ?: 0.0
    }

    fun getLastMonthCashflow(): Double {
        return records.value?.get(1)?.netWorth?.let { records.value?.get(0)?.getCashflow(it) }
            ?: 0.0
    }

}