package com.woleapp.netpos.qrgenerator.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.ui.fragments.MerchantsFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.MyTallyFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.QRFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.TransactionFragment

class DashboardViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return if (BuildConfig.FLAVOR.contains("tallywallet")) {
            TALLY_NUM_PAGES
        }else{
            NUM_PAGES
        }
    }

    override fun createFragment(position: Int): Fragment {
        return if (BuildConfig.FLAVOR.contains("tallywallet")){
            when (position) {
                1 -> QRFragment()
                2 -> TransactionFragment()
                3 -> MerchantsFragment()
                else -> MyTallyFragment()
            }
        }else{
            when (position) {
                1 -> TransactionFragment()
                2 -> MerchantsFragment()
                else -> QRFragment()
            }
        }
    }

    companion object {
        private const val NUM_PAGES = 3
        private const val TALLY_NUM_PAGES = 4
    }
}
