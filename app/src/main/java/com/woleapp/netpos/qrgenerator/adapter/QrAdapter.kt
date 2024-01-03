package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.databinding.QrLayoutBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponseItem
import com.woleapp.netpos.qrgenerator.model.wallet.QRTokenResponse
import com.woleapp.netpos.qrgenerator.model.wallet.QRTokenResponseItem


class QrAdapter(private var qrList: List<GenerateQRResponse>, private val qrClick: OnQrClick): RecyclerView.Adapter<QrAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: QrLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val qrTitle = binding.qrTitle
        val qrCode = binding.qrImage
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
                    Glide.with(context).load(data).into(qrCode)
                    qrTitle.text = issuing_bank + " " +card_scheme
                    val newdate = date?.substring(0,10)
                    val time = date?.substring(11,16)
                    if (time?.substring(0,2)!! < 12.toString()){
                        qrDate.text = "${newdate} $time AM"
                    } else {
                        val newTime = time.substring(0,2).toInt()
                        val calculatedTime = newTime - 12
                        val remainingTime = time.substring(3,5)
                        if (calculatedTime == 0){
                            qrDate.text = "${newdate} 12:$remainingTime PM"
                        }else if (calculatedTime < 10){
                            qrDate.text = "${newdate} 0$calculatedTime:$remainingTime PM"
                        } else{
                            qrDate.text = "${newdate} $calculatedTime:$remainingTime PM"
                        }
                    }
                    qrViewTransaction.setOnClickListener {
                        qrClick.viewTransaction(qrList[position])
                    }
                    qrViewQr.setOnClickListener {
                        qrClick.onViewQr(qrList[position])
                    }
                }
            }
        }
//        holder.itemView.setOnClickListener {
//                qrClick.onQrClicked(qrList[position])
//        }
    }

    fun notifyDataChanged(data: List<GenerateQRResponse>){
        this.qrList = data
    }

    override fun getItemCount(): Int {
        return qrList.size
    }
    interface OnQrClick {
        fun viewTransaction(qrModel: GenerateQRResponse)
        fun onViewQr(qrModel: GenerateQRResponse)
    }
}


