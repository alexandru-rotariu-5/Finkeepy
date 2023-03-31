package com.alexrotariu.finkeepy.ui.main.charts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexrotariu.finkeepy.databinding.ItemDataBinding

class DataAdapter :
    ListAdapter<Data, DataAdapter.DataGridViewHolder>(DataDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataGridViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataGridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataGridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DataGridViewHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Data) {
            binding.apply {
                tvLabel.text = data.title
                tvValue.text = data.value
            }
        }
    }

    class DataDiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }
}

