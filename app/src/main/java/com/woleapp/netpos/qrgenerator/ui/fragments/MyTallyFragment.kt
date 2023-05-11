package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.TallyWalletUserTransactionAdapter
import com.woleapp.netpos.qrgenerator.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentMyTallyBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterOtpBinding
import com.woleapp.netpos.qrgenerator.model.TransactionModel
import com.woleapp.netpos.qrgenerator.model.wallet.TallyWalletUserTransactionsResponseItem
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel

class MyTallyFragment : Fragment(), TransactionAdapter.OnTransactionClick {

    private lateinit var binding: FragmentMyTallyBinding
    private lateinit var qrAdapter: TallyWalletUserTransactionAdapter
   // private lateinit var qrAdapter: TransactionAdapter
    private lateinit var qrDataList: ArrayList<TallyWalletUserTransactionsResponseItem>
    private lateinit var qrDataLists: ArrayList<TransactionModel>
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: android.app.AlertDialog
    private var tallyWalletBalance: Int = 0
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var otp: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_tally, container, false)
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finishAffinity()
                }
            })
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        val header = Singletons().getTallyUserToken()!!
         token = "Bearer $header"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // dummyList()
        getUserTransactions(5)
        // loader = binding.myTallyProgressbar
        Singletons().getTallyWalletBalance()?.balance?.let {
            tallyWalletBalance = it
        }

        binding.addToBalance.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToAddToBalanceFragment()
            findNavController().navigate(action)
        }
        binding.displayMyQr.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToDisplayWalletQrFragment()
            findNavController().navigate(action)
        }

        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@MyTallyFragment
            executePendingBindings()
        }
        enterOTPDialog = AlertDialog.Builder(requireContext())
            .setView(enterOTPBinding.root)
            .setCancelable(false)
            .create()
        fetchWallet()

        enterOTPBinding.proceed.setOnClickListener {
            otp = enterOTPBinding.otpEdittext.text?.trim().toString()
            verifyWalletOTP(token, otp)
        }
    }

    private fun tallySetUp() {
        qrAdapter = TallyWalletUserTransactionAdapter(qrDataList)
        binding.tallyRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tallyRecycler.adapter = qrAdapter
    }

//    private fun tallySetUps() {
//        qrAdapter = TransactionAdapter(qrDataLists, this)
//        binding.tallyRecycler.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.tallyRecycler.adapter = qrAdapter
//    }


    override fun onTransactionClicked(transaction: TransactionModel) {
        //
    }

    private fun fetchWallet() {

       //     "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTcsImVtYWlsIjoiZXpla2llbEBnbWFpbC5jb20iLCJmdWxsbmFtZSI6IlRlc3QiLCJtb2JpbGVfcGhvbmUiOiIwNzAzMzQ3NDE5OCIsImlhdCI6MTY4MjQxMTUxMiwiZXhwIjoxNzEzOTQ3NTEyfQ.LLEUvp_NplojFDsWXJ02vj_QwyU8HjgY9x32QWucQwM"
        //       val header = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTcsImVtYWlsIjoiZXpla2llbEBnbWFpbC5jb20iLCJmdWxsbmFtZSI6IlRlc3QiLCJtb2JpbGVfcGhvbmUiOiIwNzA0MzQ3NDE1OCIsImlhdCI6MTY4MjQxMTUxMiwiZXhwIjoxNzEzOTQ3NTEyfQ.J_SrpzpbarRVMyeiOpIlFLoLAbf2efW0LhMlU5YEyr8"
        val apiKey = "27403342c95d1d83a40c0a8523803ec1518e2e5d6edd64b6296a81e8f94b1091"

        walletViewModel.fetchWallet(token)
        observeServerResponse(
            walletViewModel.fetchWalletResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.fetchWalletResponse.value?.let {
                if (it.data?.balance == null) {
                    enterOTPDialog.show()
                } else {
                    binding.walletBalance.text = it.data.balance.formatCurrency()
                    binding.walletNumber.text = it.data.phone_no
                }
            }
        }
    }

    private fun verifyWalletOTP(token: String, otp: String) {

        walletViewModel.verifyWalletOTP(token, otp)
        observeServerResponse(
            walletViewModel.walletOTPResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.walletOTPResponse.value?.let {
                if (it.data?.status == "Failed") {
                    showToast("Wrong OTP")
                } else {
                    enterOTPDialog.dismiss()
                    showToast("Your Phone number has been verified")
                    binding.walletBalance.text = it.data?.balance
                    binding.walletNumber.text = it.data?.tally_no
                }
            }
        }
    }

    private fun getUserTransactions(recordsNumber: Int) {

        walletViewModel.getUserTransactions(recordsNumber)
        observeServerResponse(
            walletViewModel.getUserTransactionResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.getUserTransactionResponse.value?.let {result ->
                if (result.data.isNullOrEmpty()){
                    binding.noTransactionYet.visibility = View.VISIBLE
                    binding.myTallyProgressbar.visibility = View.GONE
                }
                result.data?.let {resp ->
                    qrDataList = arrayListOf()
                    resp.forEach {
                        qrDataList.add(it)
                    }
                    tallySetUp()
                }
            }
        }
    }
}