package com.woleapp.netpos.qrgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.QrLayoutBinding
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.ui.model.QrModel
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel


class QrAdapter(private var qrList: List<QrModel>, private val qrClick: OnQrClick): RecyclerView.Adapter<QrAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: QrLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val qrTitle = binding.qrTitle
        val qrDate = binding.qrDate
        val qrViewTransaction = binding.viewTransaction
        val qrViewQr = binding.viewQr

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QrLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(qrList[position]){
                    qrTitle.text = title
                    qrDate.text = date
                    qrViewTransaction.setOnClickListener {
                        qrClick.onQrClicked(qrList[position])
                    }
                    qrViewQr.setOnClickListener {
                        qrClick.viewTransaction(qrList[position])
                    }
                }
            }
        }
//        holder.itemView.setOnClickListener {
//                qrClick.onQrClicked(qrList[position])
//        }
    }

    override fun getItemCount(): Int {
        return qrList.size
    }
    interface OnQrClick {
        fun viewTransaction(qrModel: QrModel)
        fun onQrClicked(qrModel: QrModel)
    }
}


