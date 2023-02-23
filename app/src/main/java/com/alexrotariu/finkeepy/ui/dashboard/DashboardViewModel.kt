package com.alexrotariu.finkeepy.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.data.repositories.RecordsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val recordsRepository: RecordsRepository) : ViewModel() {

    private val _netWorth = MutableLiveData<Double?>()
    val netWorth: LiveData<Double?> = _netWorth

    private val _records = MutableLiveData<List<Record?>?>()
    val records: LiveData<List<Record?>?> = _records

    init {
        getNetWorth()
        getAllRecords()
    }

    private fun getAllRecords() {
        viewModelScope.launch {
            val response = recordsRepository.getAllRecords()
            if (response != null) {
                _records.value = response
            }
        }
    }

    private fun getNetWorth() {
        viewModelScope.launch {
            val response = recordsRepository.getNetWorth()
            if (response != null) {
                _netWorth.value = response
            }
        }
    }
}