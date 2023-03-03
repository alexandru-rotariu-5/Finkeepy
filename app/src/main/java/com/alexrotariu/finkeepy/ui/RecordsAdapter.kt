package com.alexrotariu.finkeepy.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.ItemRecordBinding
import com.alexrotariu.finkeepy.utils.format
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter(private val limit: Int = 10_000) :
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

            holder.bind(record!!, previousNetWorth, position == 0)
        }
    }

    class RecordViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())

        fun bind(record: Record, previousNetWorth: Double, isFirstItem: Boolean) {
            binding.llRecordData.background = if (isFirstItem) {
                ContextCompat.getDrawable(binding.root.context, R.drawable.bg_record_data_white)
            } else {
                ContextCompat.getDrawable(binding.root.context, R.drawable.bg_record_data)
            }

            val textColor = if (isFirstItem) {
                ContextCompat.getColor(binding.root.context, R.color.turquoise)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.white)
            }

            binding.tvNetWorth.setTextColor(textColor)
            binding.tvIncome.setTextColor(textColor)
            binding.tvExpense.setTextColor(textColor)
            binding.tvCashflow.setTextColor(textColor)

            binding.tvMonth.text = dateFormat.format(record.timestamp)
            binding.tvNetWorth.text = record.netWorth.format()
            binding.tvIncome.text = record.income.format()
            binding.tvExpense.text = record.getExpense(previousNetWorth).format()
            binding.tvCashflow.text = record.getCashflow(previousNetWorth).format()
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
