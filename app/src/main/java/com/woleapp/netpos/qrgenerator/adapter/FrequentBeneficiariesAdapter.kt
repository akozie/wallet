package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.databinding.LayoutFrequentBeneficiariexBinding
import com.woleapp.netpos.qrgenerator.databinding.QrLayoutBinding
import com.woleapp.netpos.qrgenerator.model.FrequentBeneficiariesModel
import com.woleapp.netpos.qrgenerator.model.QrModel


class FrequentBeneficiariesAdapter(private var qrList: List<FrequentBeneficiariesModel>): RecyclerView.Adapter<FrequentBeneficiariesAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: LayoutFrequentBeneficiariexBinding): RecyclerView.ViewHolder(binding.root) {
        val userImage = binding.userImage
        val userName = binding.userName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutFrequentBeneficiariexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(qrList[position]){
                    userName.text = user_name
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return qrList.size
    }

}


