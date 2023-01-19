package com.woleapp.netpos.qrgenerator.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.woleapp.netpos.qrgenerator.ui.fragments.MerchantsFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.QRFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.TransactionFragment

class DashboardViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> TransactionFragment()
            2 -> MerchantsFragment()
            else -> QRFragment()
        }
    }

    companion object {
        private const val NUM_PAGES = 3
    }
}
