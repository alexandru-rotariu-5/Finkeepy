package com.alexrotariu.finkeepy.ui.main.records

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.ItemRecordBinding
import com.alexrotariu.finkeepy.utils.format
import com.alexrotariu.finkeepy.utils.getShortMonth
import java.time.Month


class RecordAdapter(private val limit: Int = 10_000, private val fragmentManager: FragmentManager) :
    ListAdapter<Record, RecordAdapter.RecordViewHolder>(
        RecordDiffCallback()
    ) {

    private var fullList: List<Record?>? = null

    fun setFullList(list: List<Record?>?) {
        fullList = list
        submitList(list)
    }

    private fun getFullList(): List<Record?>? {
        return fullList
    }

    override fun getItemCount(): Int {
        return minOf(super.getItemCount(), limit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val fullList = getFullList()

        if (fullList != null && position < fullList.size) {
            val record = fullList[position]

            val previousNetWorth = fullList.getOrNull(position + 1)?.netWorth ?: 0.0

            holder.bind(record!!, previousNetWorth, position, fragmentManager)
        }
    }

    class RecordViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            record: Record,
            previousNetWorth: Double,
            position: Int,
            fragmentManager: FragmentManager
        ) {

            val isFirstItem = position == 0

            val cashflow = record.getCashflow(previousNetWorth)

            val primaryTextColor = ContextCompat.getColor(
                binding.root.context,
                if (isFirstItem) R.color.white else R.color.primary
            )

            val secondaryTextColor = ContextCompat.getColor(
                binding.root.context,
                if (isFirstItem) R.color.white else R.color.primary_light
            )

            with(binding) {
                llRecordData.background = ContextCompat.getDrawable(
                    binding.root.context, if (isFirstItem) {
                        R.drawable.bg_record_data_primary
                    } else {
                        R.drawable.bg_record_data_white
                    }
                )

                tvNetWorth.setTextColor(primaryTextColor)
                tvIncome.setTextColor(primaryTextColor)
                tvExpense.setTextColor(secondaryTextColor)
                tvCashflow.setTextColor(if (cashflow > 0) primaryTextColor else secondaryTextColor)

                if (record.timestamp.month == Month.DECEMBER && !isFirstItem) {
                    llYear.visibility = View.VISIBLE
                    tvYear.text = record.timestamp.plusYears(1).year.toString()
                } else {
                    llYear.visibility = View.GONE
                }

                tvMonth.text = record.timestamp.getShortMonth()
                tvNetWorth.text = record.netWorth.format()
                tvIncome.text = record.income.format()
                tvExpense.text = record.getExpense(previousNetWorth).format()
                tvCashflow.text = record.getCashflow(previousNetWorth).format()

                llRecordData.setOnClickListener {
                    onRecordClick(record, previousNetWorth, fragmentManager)
                }
            }
        }

        private fun onRecordClick(
            record: Record,
            previousNetWorth: Double,
            fragmentManager: FragmentManager
        ) {
            val bottomSheet = RecordBottomSheet(record, previousNetWorth)
            bottomSheet.show(fragmentManager, bottomSheet.tag)
        }
    }

    class RecordDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.recordId == newItem.recordId
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }
}
