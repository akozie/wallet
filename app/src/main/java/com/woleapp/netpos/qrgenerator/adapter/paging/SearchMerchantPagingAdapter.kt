package com.woleapp.netpos.qrgenerator.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.databinding.MerchantLayoutBinding
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.model.Merchant
import com.woleapp.netpos.qrgenerator.model.Transaction

class SearchMerchantPagingAdapter(private val merchantClickListener: OnMerchantClick) :
    PagingDataAdapter<Merchant, SearchMerchantPagingAdapter.SearchMerchantViewHolder>(
        differCallback
    ) {

    inner class SearchMerchantViewHolder(private val binding: MerchantLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val merchantName = binding.merchantName
        val merchantAddress = binding.merchantAddress
    }

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<Merchant>() {
            override fun areItemsTheSame(oldItem: Merchant, newItem: Merchant): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Merchant, newItem: Merchant): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMerchantViewHolder {
        val binding = MerchantLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchMerchantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMerchantViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.apply {
            with(holder) {
                merchantName.text = current?.contact_name
                merchantAddress.text = current?.address
                }
            }
        holder.itemView.setOnClickListener {
            if (current != null) {
                merchantClickListener.onEachMerchantClicked(current)
            }

        }
        }
    interface OnMerchantClick {
        fun onEachMerchantClicked(merchant: Merchant)
    }
    }




