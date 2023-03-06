package com.alexrotariu.finkeepy.ui.dashboard

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.FragmentDashboardBinding
import com.alexrotariu.finkeepy.ui.MainActivity
import com.alexrotariu.finkeepy.ui.RecordAdapter
import com.alexrotariu.finkeepy.ui.ValueType
import com.alexrotariu.finkeepy.ui.records.RecordsFragment
import com.alexrotariu.finkeepy.utils.format
import com.alexrotariu.finkeepy.utils.formatDecimalString
import com.alexrotariu.finkeepy.utils.split
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        initRecordsAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGraph()
        initObservers()
        initClickListeners()
        initSwipeListener()
    }

    private fun getViewModel() = (activity as MainActivity).viewModel

    private fun setupGraph() {
        with(binding.lcMainGraph) {
            setupGraphXAxis()

            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
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
            setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }


    private fun setupGraphXAxis() {
        with(binding.lcMainGraph.xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(true)
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? =
                    formatGraphLabelToDate(value)
            }
            textColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
    }

    private fun formatGraphLabelToDate(value: Float): String? {
        val index = value.toInt()
        if (index >= 0 && index < (getViewModel().records.value?.size ?: 0)) {
            val date = getViewModel().records.value?.reversed()?.get(index)?.timestamp
            return date?.let { DateTimeFormatter.ofPattern("MMM yy", Locale.getDefault()).format(it) }
        }
        return ""
    }


    private fun updateGraphData(data: List<Entry>, label: ValueType) {
        val dataSet = LineDataSet(data, getString(label.labelResource))

        dataSet.color = ContextCompat.getColor(requireContext(), R.color.white)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.lineWidth = 3f

        val lineData = LineData(dataSet)

        binding.lcMainGraph.data = lineData
        binding.lcMainGraph.invalidate()
    }

    private fun initRecordsAdapter() {
        recordAdapter = RecordAdapter(RECORDS_LIMIT)
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }
    }

    private fun initSwipeListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getViewModel().getRecords {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initClickListeners() {
        binding.tvSeeAllRecords.setOnClickListener {
            (activity as MainActivity).openFragment(RecordsFragment())
        }
    }

    private fun initObservers() {
        getViewModel().records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateNetWorthView(getViewModel().getNetWorth())
                updateLastMonthCashflowView(getViewModel().getLastMonthCashflow())
                updateRecords(records)
                updateGraphData(
                    getViewModel().getChartEntries(),
                    getViewModel().graphValueType.value!!
                )
            }
        }
    }

    private fun updateNetWorthView(netWorth: Double) {
        binding.tvNetWorthWhole.text = netWorth.split().first.format()
        binding.tvNetWorthDecimal.text =
            String.format(getString(R.string.decimal), netWorth.split().second.toString().formatDecimalString())
    }

    private fun updateLastMonthCashflowView(cashflow: Double) {
        val text =
            String.format(getString(R.string.last_month_cashflow), cashflow.split().first.format())

        val formattedText = SpannableStringBuilder(text)

        formattedText.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            text.substring(0, text.indexOf(" ")).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvLastMonthCashflow.text = formattedText
        binding.ivCashflowArrow.visibility = View.VISIBLE

        if (cashflow < 0) {
            binding.ivCashflowArrow.rotation = 180f
        }
    }

    private fun updateRecords(records: List<Record?>?) {
        recordAdapter.setFullList(records)
    }

    companion object {
        const val RECORDS_LIMIT = 3
    }
}