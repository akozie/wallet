package com.woleapp.netpos.qrgenerator.adapter


//class QrDetailsViewPagerAdapter : ListAdapter<QrDetailsModel, QrDetailsViewPagerAdapter.ItemViewHolder>(CarListComparator()) {
//
//    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
//
//    override fun onCreateViewHolder( viewGroup: ViewGroup, i: Int): ItemViewHolder {
//        val binding = QrDetailsLayoutBinding.inflate(
//            LayoutInflater.from(viewGroup.context),
//            viewGroup,
//            false
//        )
//        return ItemViewHolder(binding)
//    }
//
//    override fun onBindViewHolder( itemViewHolder: ItemViewHolder, i: Int) {
//        val currentItem = getItem(i)
//        if (currentItem != null) {
//            itemViewHolder.bind(currentItem)
//        }
//
//        // Create layout manager with initial prefetch item count
//        val layoutManager = LinearLayoutManager(
//            itemViewHolder.qrSubItemRecyclerView.context,
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//        layoutManager.initialPrefetchItemCount = currentItem.transactions.transactions.size
//
//        // Create sub item view adapter
//        val subItemAdapter = currentItem.transactions.let { QrDetailsSubAdapter(it.transactions) }
//        itemViewHolder.qrSubItemRecyclerView.layoutManager = layoutManager
//        itemViewHolder.qrSubItemRecyclerView.adapter = subItemAdapter
//        itemViewHolder.qrSubItemRecyclerView.setRecycledViewPool(viewPool)
//    }
//
//    inner class ItemViewHolder(private var binding: QrDetailsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
//        val qrBankCard= binding.qrDetailsBankCard
//        val qrImage= binding.qrDetailsImage
//        val qrSubItemRecyclerView: RecyclerView = binding.qrRecyclerview
//
//        fun bind(item: QrDetailsModel) {
//            binding.apply {
//                qrBankCard.text = item.bankCard
//                Picasso.get().load(item.image).into(qrImage)
//            }
//        }
//    }
//
//    class CarListComparator : DiffUtil.ItemCallback<QrDetailsModel>() {
//        override fun areItemsTheSame(oldItem: QrDetailsModel, newItem: QrDetailsModel) =
//            oldItem.id == newItem.id
//
//        override fun areContentsTheSame(oldItem: QrDetailsModel, newItem: QrDetailsModel) =
//            oldItem == newItem
//    }
//}

