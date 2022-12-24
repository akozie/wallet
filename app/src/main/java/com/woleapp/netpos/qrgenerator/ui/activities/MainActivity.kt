package com.woleapp.netpos.qrgenerator.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.ActivityMainBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.DashboardViewPagerAdapter
import com.woleapp.netpos.qrgenerator.ui.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private val generateQrViewModel by viewModels<QRViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        generateQrViewModel.getCardSchemes()
        generateQrViewModel.getCardBanks()
        setContentView(binding.root)
    }

}