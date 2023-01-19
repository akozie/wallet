package com.woleapp.netpos.qrgenerator.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.woleapp.netpos.qrgenerator.ui.fragments.*

class TransactionHistoryPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AllTransactionsFragment()
            else -> RecentTransactionsFragment()
        }
    }

    companion object {
        private const val NUM_PAGES = 2
    }
}
