package com.alexrotariu.finkeepy.ui.main.records

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.BottomSheetRecordBinding
import com.alexrotariu.finkeepy.utils.LayoutUtils
import com.alexrotariu.finkeepy.utils.format
import com.alexrotariu.finkeepy.utils.getShortMonthAndYear
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val CASHFLOW_CHART_HEIGHT = 120

class RecordBottomSheet(
    private val record: Record,
    private val previousNetWorth: Double
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                bottomSheet.setBackgroundResource(R.drawable.bg_bottom_sheet_record)
            }
        }

        return bottomSheetDialog
    }

    private fun setClickListeners() {
        binding.vIncome.setOnClickListener {
            it.bringToFront()
        }

        binding.vExpense.setOnClickListener {
            it.bringToFront()
        }
    }

    private fun initViews() {
        binding.tvDate.text = record.timestamp.getShortMonthAndYear()
        setupNetWorthView()
        setupCashflowChart()
        setupCashflowViews()
    }

    private fun setupCashflowChart() {
        val income = record.income
        val expense = record.getExpense(previousNetWorth)

        val max: Double = income.coerceAtLeast(expense)

        val ratio = (if (max == income) expense else income) / max

        val maxHeight =
            (CASHFLOW_CHART_HEIGHT * LayoutUtils.getDpToPxDensity(binding.root.context)).toInt()
        val minHeight = (ratio * maxHeight).toInt()

        binding.apply {
            vIncome.layoutParams.height = if (income > expense) maxHeight else minHeight
            vExpense.layoutParams.height = if (expense > income) maxHeight else minHeight

            if (income >= expense) {
                vExpense.bringToFront()
            } else {
                vIncome.bringToFront()
            }

            tvIncomeValue.text = String.format(
                getString(R.string.amount_with_currency),
                income.format(),
                getString(R.string.currency_ron)
            )

            tvExpenseValue.text = String.format(
                getString(R.string.amount_with_currency),
                expense.format(),
                getString(R.string.currency_ron)
            )

            vIncome.requestLayout()
            vExpense.requestLayout()
        }
    }

    private fun setupCashflowViews() {
        val cashflow = record.getCashflow(previousNetWorth)

        binding.apply {
            tvCashflowLabel.text = getString(
                if (cashflow > 0) R.string.your_net_worth_increased_by else R.string.your_net_worth_decreased_by
            )

            llCashflow.setBackgroundResource(
                if (cashflow > 0) R.drawable.bg_rounded_primary else R.drawable.bg_rounded_primary_light
            )

            tvCashflowValue.text = getString(
                R.string.amount_with_currency,
                cashflow.format(),
                getString(R.string.currency_ron)
            )

            ivCashflowArrow.apply {
                if (cashflow < 0) {
                    setImageResource(R.drawable.ic_double_arrow_down_white)
                } else if (cashflow > 0) {
                    setImageResource(R.drawable.ic_double_arrow_up_white)
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    private fun setupNetWorthView() {
        val netWorth = record.netWorth

        binding.tvNetWorth.text = getString(
            R.string.amount_with_currency,
            netWorth.format(),
            getString(R.string.currency_ron)
        )
    }
}