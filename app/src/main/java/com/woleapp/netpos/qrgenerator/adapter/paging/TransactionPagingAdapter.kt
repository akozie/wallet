package com.woleapp.netpos.qrgenerator.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.model.Transaction

class TransactionPagingAdapter :
    PagingDataAdapter<Transaction, TransactionPagingAdapter.TransactionViewHolder>(
        differCallback
    ) {


    inner class TransactionViewHolder(private val binding: TransactionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val transactionTitle = binding.transactionTitle
        val transactionDate = binding.transactionDate
        val transactionPrice = binding.transactionPrice

    }

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.rrn == newItem.rrn
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.apply {
            with(holder) {
                    transactionTitle.text = current?.transactionType.toString()
                    transactionDate.text = current?.transactionTime
                    transactionPrice.text = current?.transmissionDateTime
                }
            }
        }

}





