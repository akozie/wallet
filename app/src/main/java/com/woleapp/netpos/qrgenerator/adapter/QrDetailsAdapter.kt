package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.model.Transaction
import com.woleapp.netpos.qrgenerator.model.TransactionModel


class QrDetailsAdapter(private var transactionList: List<Transaction>): RecyclerView.Adapter<QrDetailsAdapter.MyViewHolder>() {

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
//                    transactionTitle.text = transactionType.toString()
                        val date = transmissionDateTime?.substring(0, 10)
                    val time = transmissionDateTime?.substring(11,16)
                    if (time?.substring(0,2)!! < 12.toString()){
                        transactionDate.text = date + " "+ time + " AM"
                    } else {
                        val newTime = time.substring(0,2).toInt()
                        val calculatedTime = newTime - 12
                        val remainingTime = time.substring(3,5)
                        if (calculatedTime == 0){
                            transactionDate.text  = date+ " 12:$remainingTime PM"
                        }else if (calculatedTime < 10){
                            transactionDate.text = date + " 0$calculatedTime:$remainingTime PM"
                        } else {
                            transactionDate.text = date +" $calculatedTime:$remainingTime PM"
                        }
                    }
                    transactionPrice.text = amount.toString()
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


