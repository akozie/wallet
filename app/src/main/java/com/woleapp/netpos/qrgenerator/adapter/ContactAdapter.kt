package com.woleapp.netpos.qrgenerator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.databinding.ContactLayoutBinding
import com.woleapp.netpos.qrgenerator.model.ContactModel


class ContactAdapter(
    private var contactList: MutableList<ContactModel>,
    private val showShareButton: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    private var isEnable = false
    private val itemSelectedList = mutableListOf<Int>()
    private lateinit var selectedItem: ArrayList<ContactModel>

     class MyViewHolder(private val binding: ContactLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contactName = binding.contactName
        val contactNumber = binding.contactNumber
        val contactLayout = binding.contactLayout
        val contactSelectIcon = binding.contactImage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ContactLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = contactList[position]
        holder.itemView.apply {
            with(holder) {
                with(item) {
                    contactName.text = name
                    contactNumber.text = phoneNumber
                }
                selectedItem = arrayListOf()

                if (item.selected){
                    holder.contactSelectIcon.visibility = View.VISIBLE
                    selectedItem = arrayListOf()
                    selectedItem.add(item)
                    showShareButton(true)
                }else{
                    holder.contactSelectIcon.visibility = View.GONE
                    selectedItem.remove(item)
//                    if (selectedItem.isEmpty()) {
//                        showShareButton(false)
//                    }
                }

                itemView.setOnClickListener {
                    selectItem(position)
                }
            }
        }

//        holder.itemView.setOnLongClickListener {
//          //  selectItem(holder, item, position)
//            selectItem(adapterPostion)
//            true
//        }

//        holder.contactLayout.setOnClickListener {
//            if (itemSelectedList.contains(position)){
//                itemSelectedList.remove(position)
//                holder.contactSelectIcon.visibility = View.GONE
//                item.selected = false
//                if (itemSelectedList.isEmpty()){
//                    isEnable = false
//                    showShareButton(false)
//                }
//            }else {
//                selectItem(holder, item, position)
//            }
//        }
    }

    private fun selectItem(holder: ContactAdapter.MyViewHolder, item: ContactModel, position: Int) {
        isEnable = true
        itemSelectedList.add(position)
        holder.contactSelectIcon.visibility = View.VISIBLE
        item.selected = true
        showShareButton(true)
    }

    private fun selectItem(adapterPosition: Int){
        contactList[adapterPosition].selected = !contactList[adapterPosition].selected

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

//    fun notifyDataChanged(data: List<ContactModel>) {
//        this.contactList = data
//    }
//
//    fun showList(){
//        if (itemSelectedList.isNotEmpty()){
//            contactList.filter {item ->
//                Log.d("CHECKNOW", "${item.name}")
//                item.selected
//            }
//        }
//    }




}


