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

class DashboardViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val NUM_PAGES: Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return if (NUM_PAGES == 4){
            when (position) {
                1 -> MyTallyFragment()
                2 -> TransactionFragment()
                3 -> MerchantsFragment()
                else -> QRFragment()
            }
        }else{
            when (position) {
                1 -> TransactionFragment()
                2 -> MerchantsFragment()
                else -> QRFragment()
            }
        }
    }

}
