package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.DashboardViewPagerAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionsBinding


class TransactionsFragment : Fragment() {

    private lateinit var _binding: FragmentTransactionsBinding
    private val binding get() = _binding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        viewPager2 = binding.pager
        tabLayout = binding.tabs

        val adapter = DashboardViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        if (BuildConfig.FLAVOR.contains("tallywallet")){
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.my_tally)
                    1 -> tab.text = getString(R.string.qr_code)
                    2 -> tab.text = getString(R.string.transactions)
                    3 -> tab.text = getString(R.string.merchants)
                }
            }.attach()
        }else{
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.qr_code)
                    1 -> tab.text = getString(R.string.transactions)
                    2 -> tab.text = getString(R.string.merchants)
                }
            }.attach()
        }
    }
}