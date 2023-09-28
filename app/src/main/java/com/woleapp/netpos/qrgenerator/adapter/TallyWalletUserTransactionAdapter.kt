package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.model.Merchant
import com.woleapp.netpos.qrgenerator.model.wallet.TallyWalletUserTransactionsResponseItem
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatDate


class TallyWalletUserTransactionAdapter(private var transactionList: List<TallyWalletUserTransactionsResponseItem>, private val onUserTransactionsClick :OnUserTransactionsClick): RecyclerView.Adapter<TallyWalletUserTransactionAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: TransactionLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val transactionTitle = binding.transactionTitle
        val transactionDate = binding.transactionDate
        val transactionPrice = binding.transactionPrice
        val transImage = binding.transactionType

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TransactionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(transactionList[position]){
                    val date = created_at.substring(0, 10)
                    val time = created_at.substring(11, 16)
                    if (time.substring(0, 2) < 12.toString()){
                        transactionDate.text = "$date \n$time AM"
                    } else {
                        val newTime = time.substring(0,2).toInt()
                        val calculatedTime = newTime - 12
                        val remainingTime = time.substring(3,5)
                        if (calculatedTime == 0){
                            transactionDate.text = "$date \n12:$remainingTime PM"
                        }else if (calculatedTime < 10){
                            transactionDate.text = "$date \n0$calculatedTime:$remainingTime PM"
                        } else{
                            transactionDate.text = "$date \n$calculatedTime:$remainingTime PM"
                        }
                    }
                    transactionTitle.text = source_acct
                    transactionPrice.text = transaction_amount.toInt().formatCurrency()
                    if (transaction_type == "credit"){
                        transImage.setImageResource(R.drawable.credit)
                    }else{
                        transImage.setImageResource(R.drawable.debit)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            onUserTransactionsClick.onEachTransactionsClicked(transactionList[position])
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

}

interface OnUserTransactionsClick {
    fun onEachTransactionsClicked(data: TallyWalletUserTransactionsResponseItem)
}

