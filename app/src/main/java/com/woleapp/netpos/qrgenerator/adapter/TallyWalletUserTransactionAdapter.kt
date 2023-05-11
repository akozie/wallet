package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.model.wallet.TallyWalletUserTransactionsResponseItem
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency


class TallyWalletUserTransactionAdapter(private var transactionList: List<TallyWalletUserTransactionsResponseItem>): RecyclerView.Adapter<TallyWalletUserTransactionAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: TransactionLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val transactionTitle = binding.transactionTitle
        val transactionDate = binding.transactionDate
        val transactionPrice = binding.transactionPrice

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TransactionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(transactionList[position]){
                    transactionTitle.text = source_acct
                    transactionDate.text = created_at
                    transactionPrice.text = transaction_amount.toInt().formatCurrency()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

}


