package com.woleapp.netpos.qrgenerator.adapter

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
