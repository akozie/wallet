package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.BankCardAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentWithdrawalBinding
import com.woleapp.netpos.qrgenerator.model.RowX
import com.woleapp.netpos.qrgenerator.model.wallet.request.FindAccountNumberRequest
import com.woleapp.netpos.qrgenerator.model.wallet.request.OtherBanksRequest
import com.woleapp.netpos.qrgenerator.model.wallet.request.ProvidusRequest
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

class WithdrawalFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawalBinding
    private val viewModel by activityViewModels<WalletViewModel>()
    private val generateQrViewModel by activityViewModels<QRViewModel>()
    private lateinit var beneficiaryAccountNumber: TextInputEditText
    private lateinit var beneficiaryName: TextInputEditText
    private lateinit var beneficiaryAmount: TextInputEditText
    private lateinit var qrIssuingBank: AutoCompleteTextView
    private lateinit var transactionNarration: TextInputEditText
    private lateinit var loader: AlertDialog
    private lateinit var bankCard: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        //  getAccountName()
//        if (binding.accountNumber.text?.trim().toString().isNullOrEmpty()) {
//            showToast("Please enter your account number")
//        }
//        Log.d("CALLEDPRO", binding.issuingBank.text?.trim().toString().length.toString())
//        Log.d("CALLEDPROSTRING", binding.accountNumber.text?.trim().toString())
        val test = binding.accountNumber.text.toString()
    //    Log.d("TESTTTT", test)
        if (binding.accountNumber.text?.length == 10) {
            fetchAccountName()
        }
        generateQrViewModel.getCardBanks()
        binding.withdrawalBtn.setOnClickListener {
            if (binding.constraintLayout.isVisible){
                makeWithdrawal()
            }else{
                fetchAccountName()
            }
       //     showToast(binding.accountNumber.text.toString())

        }
        viewModel.fetchWalletMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
        generateQrViewModel.issuingBankResponse.observe(viewLifecycleOwner) {
            val bankCardAdapter = BankCardAdapter(
                generateQrViewModel.issuingBank, requireContext(),
                android.R.layout.simple_expandable_list_item_1
            )
            qrIssuingBank.setAdapter(bankCardAdapter)
        }
        qrIssuingBank.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category = adapterView?.getItemAtPosition(p2) as RowX
                bankCard = category.bank_id
                Log.d("CALLEDPROCHECK", bankCard)
                val test = binding.accountNumber.text.toString()
                Log.d("TESTTTT", test)
            }
        }
    }

    private fun initViews() {
        with(binding) {
            beneficiaryAccountNumber = accountNumber
            beneficiaryName = accountName
            beneficiaryAmount = transactionAmount
            qrIssuingBank = issuingBank
            transactionNarration = narration
        }
    }

//    private fun getAccountName() {
//        if (beneficiaryaccountNumber.text?.trim().toString().isNullOrEmpty()) {
//            showToast("Please enter your account number")
//        }else if (beneficiaryaccountNumber.text?.length == 10) {
//                fetchAccountName()
//        }
//    }

    private fun fetchAccountName() {
        val fetchAccount = fetchAccount()
        if (qrIssuingBank.text.contains("providus", true)) {
            viewModel.fetchProvidusAccount(fetchAccount)
            observeServerResponse(
                viewModel.fetchProvidusAccountResponse,
                loader,
                requireActivity().supportFragmentManager
            ) {
                viewModel.fetchProvidusAccountResponse.value?.data?.let {
                    beneficiaryName.setText(it.accountName)
                    binding.constraintLayout.visibility = View.VISIBLE
                }
            }
        } else {
            viewModel.fetchOtherAccount(fetchAccount)
            observeServerResponse(
                viewModel.fetchOtherAccountResponse,
                loader,
                requireActivity().supportFragmentManager
            ) {
                viewModel.fetchOtherAccountResponse.value?.data?.let {
                    beneficiaryName.setText(it.accountName)
                    binding.constraintLayout.visibility = View.VISIBLE
                }
            }
        }

    }


    private fun makeWithdrawal() {
        when {
            beneficiaryAccountNumber.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_account_number))
            }
            beneficiaryName.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_account_name))
            }
            beneficiaryAmount.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_amount))
            }
            qrIssuingBank.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_issuing_bank))
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    withdrawal()
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        beneficiaryAccountNumber.doOnTextChanged { _, _, _, _ ->
            when {
                beneficiaryAccountNumber.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_account_number))
                    isValidated = false
                }
                else -> {
                    binding.fragmentNumber.error = null
                    isValidated = true
                }
            }
        }
        beneficiaryName.doOnTextChanged { _, _, _, _ ->
            when {
                beneficiaryName.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_account_name))
                    isValidated = false
                }
                else -> {
                    binding.fragmentAccountName.error = null
                    isValidated = true
                }
            }
        }
        beneficiaryAmount.doOnTextChanged { _, _, _, _ ->
            when {
                beneficiaryAmount.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_amount))
                    isValidated = false
                }
                else -> {
                    binding.fragmentTransactionAmount.error = null
                    isValidated = true
                }
            }
        }
        qrIssuingBank.doOnTextChanged { _, _, _, _ ->
            when {
                qrIssuingBank.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_issuing_bank))
                    isValidated = false
                }
                else -> {
                    binding.fragmentSelectBank.error = null
                    isValidated = true
                }
            }
        }
        return isValidated
    }

    private fun withdrawal() {
        val providusRequest = providusModel()
        val otherBanksRequest = otherBanksModel()
        if (qrIssuingBank.text.contains("providus", true)) {
            viewModel.providusToProvidus(providusRequest)
        } else {
            viewModel.providusToOtherBanks(otherBanksRequest)
        }
        observeServerResponse(
            viewModel.providusToProvidusResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
//            viewModel.providusToProvidusResponse.value?.data?.let {
//
//            }
            findNavController().popBackStack()
        }
        observeServerResponse(
            viewModel.providusToOtherBanksResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
//            viewModel.providusToProvidusResponse.value?.data?.let {
//
//            }
            findNavController().popBackStack()
        }
    }

    private fun fetchAccount(): FindAccountNumberRequest =
        FindAccountNumberRequest(
            accountNumber = beneficiaryAccountNumber.text?.trim().toString(),
            beneficiaryBank = bankCard
        )


    private fun providusModel(): ProvidusRequest =
        ProvidusRequest(
            creditAccount = beneficiaryAccountNumber.text?.trim().toString(),
            creditAccountName = beneficiaryName.text?.trim().toString(),
            narration = transactionNarration.text?.trim().toString(),
            transactionAmount = beneficiaryAmount.text?.trim().toString()
        )

    private fun otherBanksModel(): OtherBanksRequest =
        OtherBanksRequest(
            beneficiaryAccountName = beneficiaryName.text?.trim().toString(),
            beneficiaryAccountNumber = beneficiaryAccountNumber.text?.trim().toString(),
            beneficiaryBankCode = bankCard,
            beneficiaryBankName = qrIssuingBank.text?.trim().toString(),
            narration = transactionNarration.text?.trim().toString(),
            transactionAmount = beneficiaryAmount.text?.trim().toString()
        )

}