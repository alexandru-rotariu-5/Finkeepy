package com.alexrotariu.finkeepy.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexrotariu.finkeepy.data.repositories.RecordsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val recordsRepository: RecordsRepository) : ViewModel() {

    private val _netWorth = MutableLiveData<Double?>()
    val netWorth: LiveData<Double?> = _netWorth

    init {
        getNetWorth()
    }

    private fun getAllRecords() {
        viewModelScope.launch {
            recordsRepository.getAllRecords()
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