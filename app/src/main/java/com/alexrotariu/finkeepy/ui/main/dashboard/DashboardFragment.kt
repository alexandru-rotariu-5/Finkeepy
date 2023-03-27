package com.alexrotariu.finkeepy.ui.main.dashboard

import android.animation.ValueAnimator
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
import com.alexrotariu.finkeepy.ui.main.MainActivity
import com.alexrotariu.finkeepy.ui.main.records.RecordAdapter
import com.alexrotariu.finkeepy.ui.models.Screen
import com.alexrotariu.finkeepy.ui.models.ValueType
import com.alexrotariu.finkeepy.utils.StringUtils
import com.alexrotariu.finkeepy.utils.capitalize
import com.alexrotariu.finkeepy.utils.format
import com.alexrotariu.finkeepy.utils.formatDecimalString
import com.alexrotariu.finkeepy.utils.split
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordAdapter: RecordAdapter

    private val animationDuration = 1500
    private var valueAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecordsAdapter()
        setupChart()
        initObservers()
        initClickListeners()
    }

    private fun getViewModel() = (activity as MainActivity).viewModel

    private fun isNetWorthAnimationPlayed() =
        (activity as MainActivity).isDashboardNetWorthAnimationPlayed()

    private fun setNetWorthAnimationPlayed() =
        (activity as MainActivity).setDashboardNetWorthAnimationPlayed()

    private fun isChartAnimationPlayed() =
        (activity as MainActivity).isDashboardChartAnimationPlayed()

    private fun setChartAnimationPlayed() =
        (activity as MainActivity).setDashboardChartAnimationPlayed()

    private fun setupChart() {
        binding.lcMainChart.apply {
            setupChartXAxis()

            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)
                textColor = ContextCompat.getColor(requireContext(), R.color.normal_text)
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

            setTouchEnabled(false)

            setNoDataText(getString(R.string.no_records_available))
            setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        }
    }


    private fun setupChartXAxis() {
        binding.lcMainChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(true)
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? =
                    formatChartLabelToDate(value)
            }
            textColor = ContextCompat.getColor(requireContext(), R.color.normal_text)
        }
    }

    private fun formatChartLabelToDate(value: Float): String? {
        val index = value.toInt()
        if (index >= 0 && index < (getViewModel().records.value?.size ?: 0)) {
            val date = getViewModel().records.value?.reversed()?.get(index)?.timestamp
            return date?.let {
                DateTimeFormatter.ofPattern("MMM yy", Locale.getDefault()).format(it)
            }
        }
        return ""
    }


    private fun updateChartData(data: List<Entry>) {
        val dataSet = LineDataSet(data, getString(R.string.net_worth))

        dataSet.apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            lineWidth = 3f
        }

        val lineData = LineData(dataSet)

        binding.lcMainChart.data = lineData

        if (!isChartAnimationPlayed()) {
            animateLineChart()
            setChartAnimationPlayed()
        }

        binding.lcMainChart.invalidate()
    }

    private fun animateLineChart() {
        binding.lcMainChart.animateY(animationDuration, Easing.EaseInOutCubic)
    }


    private fun initRecordsAdapter() {
        recordAdapter = RecordAdapter(RECORDS_LIMIT, childFragmentManager)
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }
    }

    private fun initClickListeners() {
        binding.tvSeeAllRecords.setOnClickListener {
            (activity as MainActivity).goToScreen(Screen.RECORDS)
        }
    }

    private fun initObservers() {
        getViewModel().records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateNetWorthView(getViewModel().getNetWorth())
                updateLastMonthCashflowView(getViewModel().getLastMonthCashflow())
                updateRecords(records)
                updateChartData(getViewModel().getChartEntries(ValueType.NET_WORTH).takeLast(5))
            }
        }
    }

    private fun updateNetWorthView(netWorth: Double) {
        if (!isNetWorthAnimationPlayed()) {
            setNetWorthViewWithAnimation(netWorth)
        } else {
            setNetWorthViewWithoutAnimation(netWorth)
        }
    }

    private fun setNetWorthViewWithAnimation(netWorth: Double) {
        valueAnimator = ValueAnimator.ofFloat(0f, netWorth.toFloat())
        valueAnimator?.duration = animationDuration.toLong()
        valueAnimator?.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            val animatedNetWorth = animatedValue.toDouble()
            binding.tvNetWorthWhole.text = animatedNetWorth.split().first.format()
            binding.tvNetWorthDecimal.text =
                String.format(
                    getString(R.string.decimal),
                    animatedNetWorth.split().second.toString().formatDecimalString()
                )
        }
        valueAnimator?.start()
        setNetWorthAnimationPlayed()
    }

    private fun setNetWorthViewWithoutAnimation(netWorth: Double) {
        binding.tvNetWorthWhole.text = netWorth.split().first.format()
        binding.tvNetWorthDecimal.text =
            String.format(
                getString(R.string.decimal),
                netWorth.split().second.toString().formatDecimalString()
            )
    }

    private fun updateLastMonthCashflowView(cashflow: Double) {
        val firstRecord = getViewModel().records.value?.firstOrNull()

        var text = StringUtils.EMPTY
        val formattedCashflow = cashflow.split().first.format()

        if (firstRecord != null) {
            text =
                if (firstRecord.timestamp.year == Year.now().value && firstRecord.timestamp.month == LocalDate.now().month) {
                    getString(R.string.this_month_cashflow, formattedCashflow)
                } else if (firstRecord.timestamp.year == Year.now().value && firstRecord.timestamp.month == LocalDate.now().month.minus(
                        1
                    )
                ) {
                    getString(R.string.last_month_cashflow, formattedCashflow)
                } else {
                    getString(
                        R.string.month_cashflow,
                        formattedCashflow,
                        firstRecord.timestamp.month.toString().capitalize()
                    )
                }
        }

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
            binding.ivCashflowArrow.setImageResource(R.drawable.ic_double_arrow_down_primary)
        } else if (cashflow > 0) {
            binding.ivCashflowArrow.setImageResource(R.drawable.ic_double_arrow_up_primary)
        } else {
            binding.ivCashflowArrow.visibility = View.GONE
        }
    }

    private fun updateRecords(records: List<Record?>?) {
        recordAdapter.setFullList(records)
    }

    override fun onDestroyView() {
        valueAnimator?.cancel()
        super.onDestroyView()
    }

    companion object {
        const val RECORDS_LIMIT = 3
    }
}