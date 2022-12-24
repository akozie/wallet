package com.woleapp.netpos.qrgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.ui.model.QrDetailsModel
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel
import com.woleapp.netpos.qrgenerator.ui.model.Transactions


class QrDetailsAdapter(private var transactionList: List<TransactionModel>): RecyclerView.Adapter<QrDetailsAdapter.MyViewHolder>() {

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
                    transactionTitle.text = title
                    transactionDate.text = date
                    transactionPrice.text = price
                }
            }
        }
//        holder.itemView.setOnClickListener {
//            clickListener.onTransactionClicked(transactionList[position])
//        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
//    interface OnTransactionClick {
//        fun onTransactionClicked(transaction: TransactionModel)
//    }
}


