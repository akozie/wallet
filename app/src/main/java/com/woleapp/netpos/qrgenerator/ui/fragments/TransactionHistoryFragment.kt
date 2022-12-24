package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionHistoryBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionsBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.DashboardViewPagerAdapter
import com.woleapp.netpos.qrgenerator.ui.adapter.TransactionHistoryPagerAdapter

class TransactionHistoryFragment : Fragment() {

    private lateinit var _binding: FragmentTransactionHistoryBinding
    private val binding get() = _binding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        viewPager2 = binding.pager
        tabLayout = binding.tabs

        val adapter = TransactionHistoryPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.seven_days)
                1 -> tab.text = getString(R.string.thirty_days)
            }
        }.attach()

    }

}