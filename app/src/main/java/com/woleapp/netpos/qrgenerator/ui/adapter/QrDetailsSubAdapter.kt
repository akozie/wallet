package com.woleapp.netpos.qrgenerator.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.woleapp.netpos.qrgenerator.databinding.QrDetailsLayoutBinding
import com.woleapp.netpos.qrgenerator.databinding.QrLayoutBinding
import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
import com.woleapp.netpos.qrgenerator.ui.model.QrDetailsModel
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel

//class QrDetailsSubAdapter(private val itemList: List<TransactionModel>):
//    RecyclerView.Adapter<QrDetailsSubAdapter.ItemViewHolder?>() {
//    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
//
//    inner class ItemViewHolder(private val binding: TransactionLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
//        val qrImageView: ImageView = binding.transactionImage
//        val qrPrice = binding.transactionPrice
//        val qrDate = binding.transactionDate
//        val qrTitle = binding.transactionTitle
//    }
//
//    override fun onCreateViewHolder( viewGroup: ViewGroup, i: Int): ItemViewHolder {
//        val view =
//            TransactionLayoutBinding.inflate(LayoutInflater.from(viewGroup.context),
//                viewGroup,
//                false)
//        return ItemViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onBindViewHolder( itemViewHolder: ItemViewHolder, i: Int) {
//        val item = itemList[i]
//       // Picasso.get().load(item.).into(itemViewHolder.qrImageView)
//        itemViewHolder.qrDate.text = item.date
//        itemViewHolder.qrPrice.text = item.price
//        itemViewHolder.qrTitle.text = item.title
//    }
//}
