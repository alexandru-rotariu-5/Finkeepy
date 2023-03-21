package com.alexrotariu.finkeepy.ui.main.graphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexrotariu.finkeepy.ui.models.GraphType
import com.alexrotariu.finkeepy.ui.models.ValueType
import javax.inject.Inject

class GraphsViewModel @Inject constructor() : ViewModel() {

    private val _graphValueTypes =
        MutableLiveData(mutableListOf(ValueType.NET_WORTH))
    val graphValueTypes: LiveData<MutableList<ValueType>> = _graphValueTypes

    private val _graphType = MutableLiveData(GraphType.LINE)
    val graphType: LiveData<GraphType> = _graphType

    fun toggleValueType(valueType: ValueType) {
        _graphValueTypes.value?.let {
            if (it.contains(valueType)) {
                if (it.size > 1) {
                    it.remove(valueType)
                }
            } else {
                it.add(valueType)
            }
            _graphValueTypes.value = it
        }
    }

    fun setGraphType(graphType: GraphType) {
        _graphType.value = graphType
    }
}