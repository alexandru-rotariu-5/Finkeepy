package com.alexrotariu.finkeepy.ui.main.graphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexrotariu.finkeepy.ui.models.ValueType
import javax.inject.Inject

class GraphsViewModel @Inject constructor() : ViewModel() {

    private val _graphValueTypes =
        MutableLiveData(mutableListOf(ValueType.NET_WORTH))
    val graphValueTypes: LiveData<MutableList<ValueType>> = _graphValueTypes

    fun toggleValueType(valueType: ValueType) {
        _graphValueTypes.value?.let {
            if (it.contains(valueType)) {
                it.remove(valueType)
            } else {
                it.add(valueType)
            }
            _graphValueTypes.value = it
        }
    }
}