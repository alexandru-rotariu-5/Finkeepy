package com.alexrotariu.finkeepy.ui.main.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexrotariu.finkeepy.ui.models.ChartType
import com.alexrotariu.finkeepy.ui.models.ValueType
import javax.inject.Inject

class ChartsViewModel @Inject constructor() : ViewModel() {

    private val _chartValueTypes =
        MutableLiveData(mutableListOf(ValueType.NET_WORTH))
    val chartValueTypes: LiveData<MutableList<ValueType>> = _chartValueTypes

    private val _chartType = MutableLiveData(ChartType.LINE)
    val chartType: LiveData<ChartType> = _chartType

    fun toggleValueType(valueType: ValueType) {
        _chartValueTypes.value?.let {
            if (it.contains(valueType)) {
                if (it.size > 1) {
                    it.remove(valueType)
                }
            } else {
                it.add(valueType)
            }
            _chartValueTypes.value = it
        }
    }

    fun setChartType(chartType: ChartType) {
        _chartType.value = chartType
    }
}