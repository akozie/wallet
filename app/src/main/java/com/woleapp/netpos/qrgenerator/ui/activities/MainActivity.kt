package com.woleapp.netpos.qrgenerator.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.paging.SearchMerchantPagingAdapter
import com.woleapp.netpos.qrgenerator.databinding.ActivityMainBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.Merchant
import com.woleapp.netpos.qrgenerator.viewmodels.MerchantViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private val generateQrViewModel by viewModels<QRViewModel>()
    private val fetchMerchantViewModel by viewModels<QRViewModel>()

    private lateinit var searchedMerchantAdapter: SearchMerchantPagingAdapter
    private val merchantViewModel by viewModels<MerchantViewModel>()
    private val mDisposable = CompositeDisposable()
    private lateinit var endLat : GenerateQRResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        generateQrViewModel.getCardSchemes()
        generateQrViewModel.getCardBanks()
//        fetchMerchantViewModel.getAllMerchant()
        setContentView(binding.root)

    }

}