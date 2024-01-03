package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.DashboardViewPagerAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionsBinding
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel


class TransactionsFragment : Fragment() {

    private lateinit var _binding: FragmentTransactionsBinding
    private val binding get() = _binding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: android.app.AlertDialog

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
        loader = alertDialog(requireContext())
        setUpViewPager()

//        for (i in 0 until tabLayout.tabCount) {
//            val tab = tabLayout.getTabAt(i)
//            if (tab != null) {
//                val tabTextView = TextView(requireContext())
//                tabTextView.text = tab.text
//                tab.customView = tabTextView
//                adjustTextSize(tabTextView, tabLayout.width / tabLayout.tabCount)
//            }
//        }
    }

    //    private fun adjustTextSize(textView: TextView, tabWidth: Int) {
//        var textSize = 70f // Initial text size
//        val textPaint = Paint()
//        val bounds = Rect()
//        while (textPaint.measureText(textView.text.toString()) > tabWidth) {
//            textPaint.setTextSize(textSize)
//            textPaint.getTextBounds(textView.text.toString(), 0, textView.text.length, bounds)
//            textSize--
//        }
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
//    }
    private fun setUpViewPager() {
        viewPager2 = binding.pager
        tabLayout = binding.tabs


        val adapter = DashboardViewPagerAdapter(childFragmentManager, lifecycle, 4)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.qr_code)
                1 -> tab.text = getString(R.string.my_tally)
                2 -> tab.text = getString(R.string.transactions)
                3 -> tab.text = getString(R.string.merchants)
            }
        }.attach()

        //  getWalletStatus()
    }

    private fun getWalletStatus() {
     //  walletViewModel.getWalletStatus()
        observeServerResponse(
            walletViewModel.getWalletStatusResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.getWalletStatusResponse.value?.let {
                val adapter = DashboardViewPagerAdapter(childFragmentManager, lifecycle, 3)
                viewPager2.adapter = adapter
                Log.d("TABRESP", it.toString())
                val response = it.data?.exists!!
                Log.d("TABLAYOUT", "TABLAYOUR")
                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.qr_code)
                        1 -> tab.text = getString(R.string.transactions)
                        2 -> tab.text = getString(R.string.merchants)
                    }
                }.attach()
            }
        }
        observeServerResponse(
            walletViewModel.getWalletUserResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            Log.d("TABRESP", "JUSTCHECKING")
            walletViewModel.getWalletUserResponse.value?.let {
                val adapter = DashboardViewPagerAdapter(childFragmentManager, lifecycle, 4)
                viewPager2.adapter = adapter
                Log.d("TABRESP", it.toString())
                val response = it.data?.exists!!
                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.my_tally)
                        1 -> tab.text = getString(R.string.qr_code)
                        2 -> tab.text = getString(R.string.transactions)
                        3 -> tab.text = getString(R.string.merchants)
                    }
                }.attach()
            }
        }
    }
}