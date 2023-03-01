package com.alexrotariu.finkeepy.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.ItemRecordBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter : ListAdapter<Record, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)

        val previousNetWorth = if (position < itemCount - 1) {
            getItem(position + 1).netWorth
        } else {
            0.0
        }

        holder.bind(record, previousNetWorth)
    }

    class RecordViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val formatter = DecimalFormat("#,###")

        fun bind(record: Record, previousNetWorth: Double) {
            binding.tvMonth.text =
                SimpleDateFormat("MMM", Locale.getDefault()).format(record.timestamp)
            binding.tvNetWorth.text = formatter.format(record.netWorth)
            binding.tvIncome.text = formatter.format(record.income)
            binding.tvExpense.text = formatter.format(record.getExpense(previousNetWorth))
            binding.tvCashflow.text = formatter.format(record.getCashflow(previousNetWorth))
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

