package com.alexrotariu.finkeepy.ui.main.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.databinding.FragmentChartsBinding
import com.alexrotariu.finkeepy.ui.main.MainActivity
import com.alexrotariu.finkeepy.ui.models.ChartType
import com.alexrotariu.finkeepy.ui.models.ValueType
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: ChartsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.applicationContext as App).appComponent.inject(this)
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initClickListeners()
        setupChart(binding.lcLineChartMultiple)
        setupChart(binding.bcBarChartMultiple)
    }

    private fun getMainViewModel() = (activity as MainActivity).viewModel

    private fun setupChart(chart: BarLineChartBase<*>) {
        chart.apply {
            setupChartXAxis(chart)

            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)
                textColor = ContextCompat.getColor(requireContext(), R.color.primary)
            }

            axisRight.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            description.isEnabled = false
            legend.isEnabled = false

            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false

            setNoDataText(getString(R.string.no_records_available))
            setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        }
    }

    private fun setupChartXAxis(chart: BarLineChartBase<*>) {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(true)
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? =
                    formatChartLabelToDate(value)
            }
            textColor = ContextCompat.getColor(requireContext(), R.color.primary)
        }
    }

    private fun formatChartLabelToDate(value: Float): String? {
        val index = value.toInt()
        if (index >= 0 && index < (getMainViewModel().records.value?.size ?: 0)) {
            val date = getMainViewModel().records.value?.reversed()?.get(index)?.timestamp
            return date?.let {
                DateTimeFormatter.ofPattern("MMM yy", Locale.getDefault()).format(it)
            }
        }
        return ""
    }

    private fun updateChartData(data: List<Pair<ValueType, List<Entry>>>) {
        val lineData = LineData()
        val barData = BarData()

        data.forEachIndexed { index, (valueType, entries) ->
            val lineDataSet = LineDataSet(entries, getString(valueType.labelResource))
            val barDataSet =
                BarDataSet(mapChartEntriesToBarEntries(entries), getString(valueType.labelResource))

            lineDataSet.apply {
                color =
                    ContextCompat.getColor(requireContext(), getChartColorResourceByIndex(index))
                setDrawCircles(false)
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                lineWidth = 3f
            }

            barDataSet.apply {
                color =
                    ContextCompat.getColor(requireContext(), getChartColorResourceByIndex(index))
                setDrawValues(false)
            }

            lineData.addDataSet(lineDataSet)
            barData.addDataSet(barDataSet)
        }

//        val spaceBetweenBars = 0f
//        val spaceBetweenGroups = 0.5f
//        val startValue = 0f // the starting value for the x-axis
//
//        if (barData.dataSetCount > 1) {
//            binding.bcBarChartMultiple.groupBars(startValue, spaceBetweenGroups, spaceBetweenBars)
//        }

        binding.lcLineChartMultiple.data = lineData
        binding.bcBarChartMultiple.data = barData

        binding.lcLineChartMultiple.invalidate()
        binding.bcBarChartMultiple.invalidate()
    }

    private fun mapChartEntriesToBarEntries(entries: List<Entry>): List<BarEntry> {
        return entries.map { entry ->
            BarEntry(entry.x, entry.y)
        }
    }

    private fun getChartColorResourceByIndex(index: Int) = when (index) {
        0 -> R.color.color_line_chart_first
        1 -> R.color.color_line_chart_second
        2 -> R.color.color_line_chart_third
        else -> R.color.color_line_chart_fourth
    }

    private fun getSelectedValueTypeViewBackgroundDrawable(valueType: ValueType) =
        when (viewModel.chartValueTypes.value?.indexOf(valueType) ?: -1) {
            0 -> R.drawable.bg_chart_value_type_selected_first
            1 -> R.drawable.bg_chart_value_type_selected_second
            2 -> R.drawable.bg_chart_value_type_selected_third
            else -> R.drawable.bg_chart_value_type_selected_fourth
        }


    private fun initObservers() {
        initRecordsObserver()
        initChartTypeObserver()
        initChartValueTypesObserver()
    }

    private fun initRecordsObserver() {
        getMainViewModel().records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateChartData(createValueTypeEntriesPairList())
            }
        }
    }

    private fun initChartValueTypesObserver() {
        viewModel.chartValueTypes.observe(viewLifecycleOwner) { valueTypes ->
            if (valueTypes != null) {
                updateChartData(createValueTypeEntriesPairList())
                updateSelectValueTypeViews()
            }
        }
    }

    private fun initChartTypeObserver() {
        viewModel.chartType.observe(viewLifecycleOwner) { type ->
            when (type) {
                ChartType.LINE -> showLineChart()
                ChartType.BAR -> showBarChart()
                else -> showLineChart()
            }

            updateSelectChartTypeViews()
        }
    }

    private fun showLineChart() {
        binding.bcBarChartMultiple.visibility = View.INVISIBLE
        binding.lcLineChartMultiple.visibility = View.VISIBLE
    }

    private fun showBarChart() {
        binding.lcLineChartMultiple.visibility = View.INVISIBLE
        binding.bcBarChartMultiple.visibility = View.VISIBLE
    }

    private fun updateSelectValueTypeView(view: TextView, valueType: ValueType) {
        view.setBackgroundResource(getSelectValueTypeViewBackground(valueType))
        view.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                getSelectedValueTypeViewTextColor(valueType)
            )
        )
    }

    private fun updateSelectValueTypeViews() {
        updateSelectValueTypeView(binding.tvSelectNetWorth, ValueType.NET_WORTH)
        updateSelectValueTypeView(binding.tvSelectIncome, ValueType.INCOME)
        updateSelectValueTypeView(binding.tvSelectExpense, ValueType.EXPENSE)
        updateSelectValueTypeView(binding.tvSelectCashflow, ValueType.CASHFLOW)
    }

    private fun getSelectValueTypeViewBackground(valueType: ValueType) =
        if (viewModel.chartValueTypes.value?.contains(valueType) == true) {
            getSelectedValueTypeViewBackgroundDrawable(valueType)
        } else {
            R.drawable.bg_chart_value_type_unselected
        }

    private fun getSelectedValueTypeViewTextColor(valueType: ValueType) =
        if (viewModel.chartValueTypes.value?.contains(valueType) == true) {
            R.color.white
        } else {
            R.color.primary
        }

    private fun updateSelectChartTypeViews() {
        updateSelectChartTypeView(binding.ivSelectLineChart, ChartType.LINE)
        updateSelectChartTypeView(binding.ivSelectBarChart, ChartType.BAR)
    }

    private fun updateSelectChartTypeView(view: ImageView, chartType: ChartType) {
        view.setBackgroundResource(getSelectChartTypeViewBackground(chartType))
        view.setImageResource(getSelectedChartTypeViewImageResource(chartType))
    }

    private fun getSelectChartTypeViewBackground(chartType: ChartType) =
        if (viewModel.chartType.value == chartType) {
            R.drawable.bg_chart_type_selected
        } else {
            R.drawable.bg_chart_type_unselected
        }

    private fun getSelectedChartTypeViewImageResource(chartType: ChartType) =
        if (viewModel.chartType.value == chartType) {
            when (chartType) {
                ChartType.LINE -> R.drawable.ic_line_chart_white
                ChartType.BAR -> R.drawable.ic_bar_chart_white
                else -> R.drawable.ic_line_chart_white
            }
        } else {
            when (chartType) {
                ChartType.LINE -> R.drawable.ic_line_chart_primary
                ChartType.BAR -> R.drawable.ic_bar_chart_primary
                else -> R.drawable.ic_line_chart_primary
            }
        }

    private fun initClickListeners() {
        binding.tvSelectNetWorth.setOnClickListener {
            viewModel.toggleValueType(ValueType.NET_WORTH)
        }

        binding.tvSelectIncome.setOnClickListener {
            viewModel.toggleValueType(ValueType.INCOME)
        }

        binding.tvSelectExpense.setOnClickListener {
            viewModel.toggleValueType(ValueType.EXPENSE)
        }

        binding.tvSelectCashflow.setOnClickListener {
            viewModel.toggleValueType(ValueType.CASHFLOW)
        }

        binding.ivSelectLineChart.setOnClickListener {
            viewModel.setChartType(ChartType.LINE)
        }

        binding.ivSelectBarChart.setOnClickListener {
            viewModel.setChartType(ChartType.BAR)
        }
    }

    private fun createValueTypeEntriesPairList() =
        viewModel.chartValueTypes.value?.map { valueType ->
            Pair(
                valueType,
                getEntriesForValueType(valueType)
            )
        } ?: emptyList()

    private fun getEntriesForValueType(valueType: ValueType) =
        getMainViewModel().getChartEntries(valueType)
}