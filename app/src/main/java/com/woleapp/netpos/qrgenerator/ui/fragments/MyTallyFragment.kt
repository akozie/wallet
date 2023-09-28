package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.tabs.TabLayoutMediator
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.*
import com.woleapp.netpos.qrgenerator.broadcastreceiver.MySMSBroadcastReceiver
import com.woleapp.netpos.qrgenerator.broadcastreceiver.MySMSBroadcastReceiver.OTPReceiveListener
import com.woleapp.netpos.qrgenerator.databinding.*
import com.woleapp.netpos.qrgenerator.model.TransactionModel
import com.woleapp.netpos.qrgenerator.model.login.UserViewModel
import com.woleapp.netpos.qrgenerator.model.wallet.NewGetSecurityQuestionResponseItem
import com.woleapp.netpos.qrgenerator.model.wallet.TallyWalletUserTransactionsResponseItem
import com.woleapp.netpos.qrgenerator.utils.NetworkConnectivityHelper
import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.displaySecurityQuestion
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.isDarkModeEnabled
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import kotlinx.coroutines.launch


class MyTallyFragment : Fragment(), TransactionAdapter.OnTransactionClick, OnUserTransactionsClick {

    private lateinit var binding: FragmentMyTallyBinding
    private lateinit var qrAdapter: TallyWalletUserTransactionAdapter
   // private lateinit var qrAdapter: TransactionAdapter
    private lateinit var qrDataList: ArrayList<TallyWalletUserTransactionsResponseItem>
    private lateinit var qrDataLists: ArrayList<TransactionModel>
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var loader: android.app.AlertDialog
    private var tallyWalletBalance: Int = 0
    private lateinit var noInternetDialog: AlertDialog
    private lateinit var noInternetBinding: LayoutNoInternetBinding
    private lateinit var token: String
    private lateinit var email:String
    private lateinit var passwordSetDialog: AlertDialog
    private lateinit var passwordSetBinding: LayoutSetTransactionPinPrefTextBinding
    private lateinit var listOfQuestions: NewGetSecurityQuestionResponseItem
    private lateinit var getSecurityQuestion: AutoCompleteTextView
    private lateinit var securityQuestionAnswer: String
    private var transactionCheckbox = false
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var otp: String

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
        loader = alertDialog(requireContext())
        val header = Singletons().getTallyUserToken(requireContext())!!
         token = "Bearer $header"


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


        passwordSetBinding = LayoutSetTransactionPinPrefTextBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@MyTallyFragment
            executePendingBindings()
        }

        passwordSetDialog =
            AlertDialog.Builder(requireContext())
                .setView(passwordSetBinding.root)
                .setCancelable(false)
                .create()

        getSecurityQuestion = passwordSetBinding.securityQuestions


        val securityQuestionAdapter = SecurityQuestionsAdapter(
            displaySecurityQuestion(), requireContext(),
            android.R.layout.simple_expandable_list_item_1
        )
        getSecurityQuestion.setAdapter(securityQuestionAdapter)

        getSecurityQuestion.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listOfQuestions =
                    adapterView?.getItemAtPosition(p2) as NewGetSecurityQuestionResponseItem
                securityQuestionAnswer = listOfQuestions.question
                passwordSetBinding.save.isEnabled = true
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletViewModel.getUserTransactionResponse.removeObservers(viewLifecycleOwner)
        Singletons().getTallyWalletBalance(requireContext())?.info?.available_balance?.let {
            tallyWalletBalance = it
        }

        passwordSetBinding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Handle checkbox state changes here
            transactionCheckbox = isChecked
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
        binding.transferButton.setOnClickListener {
            val action = TransactionsFragmentDirections.actionTransactionsFragmentToTransferFragment()
            findNavController().navigate(action)
        }
        binding.generateQrButton.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }

//
//        noInternetBinding = LayoutNoInternetBinding.inflate(
//            LayoutInflater.from(requireContext()),
//            null,
//            false
//        ).apply {
//            lifecycleOwner = this@MyTallyFragment
//            executePendingBindings()
//        }
//        noInternetDialog = AlertDialog.Builder(requireContext())
//            .setView(noInternetBinding.root)
//            .setCancelable(false)
//            .create()
//        networkConnectivityHelper.observeNetworkStatus()
//            .subscribe {
//            if (it) {
//                requireActivity().runOnUiThread(Runnable {
//                    // Stuff that updates the UI
//                    noInternetDialog.dismiss()
//                })
//                launchWhenResumed {
//                        // Do something
//                        getWalletStatus()
//                }
//            } else {
//                requireActivity().runOnUiThread(Runnable {
//                    // Stuff that updates the UI
//                    noInternetDialog.show()
//                })
//            }
//        }
//            .isDisposed

        getWalletStatus()

        passwordSetBinding.save.setOnClickListener {
            //  val savedPin = Singletons().getPin()!!
            val transactionPIN = passwordSetBinding.reprintPasswordEdittext.text.toString()
            val securityQuestion = passwordSetBinding.securityQuestions.text.toString()
            val securityAnswer = passwordSetBinding.securityAnswers.text.toString()
            val questionID = listOfQuestions.id
            if (transactionPIN.isEmpty()) {
                showToast("PIN cannot be empty")
                return@setOnClickListener
            }
            if (transactionPIN.length < 4) {
                showToast("PIN cannot be less than 4 digits")
                return@setOnClickListener
            }
            if (securityAnswer.isEmpty()) {
                showToast("Please answer your security question")
                return@setOnClickListener
            }
            if (questionID.toString().isNullOrEmpty()) {
                showToast("Please answer your security question")
                return@setOnClickListener
            }
            if (transactionPIN != passwordSetBinding.confirmReprintPasswordEdittext.text.toString()) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }
            if (!transactionCheckbox) {
                showToast("Please your consent is needed to set your transaction PIN")
                return@setOnClickListener
            }
            setTransactionPin(transactionPIN, questionID.toString(), securityQuestion, securityAnswer)
        }

        enterOTPBinding.resendOtp.setOnClickListener {
            verifyWalletAccount()
        }
        enterOTPBinding.closeDialog.setOnClickListener {
            enterOTPDialog.dismiss()
            verifyWalletOTP(token, "")
        }
        passwordSetBinding.cancelDialog.setOnClickListener {
            passwordSetDialog.dismiss()
        }

        binding.verifyPhoneNumber.setOnClickListener {
            verifyWalletAccount()
        }


        binding.setPin.setOnClickListener {
            passwordSetDialog.show()
        }

        enterOTPBinding.proceed.setOnClickListener {
            otp = enterOTPBinding.otpEdittext.text?.trim().toString()
            if (otp.isEmpty()) {
                showToast("Please enter OTP")
                return@setOnClickListener
            }
            verifyWalletOTP(token, otp)
        }
    }


    private fun tallySetUp() {
        qrAdapter = TallyWalletUserTransactionAdapter(qrDataList, this)
        binding.tallyRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tallyRecycler.adapter = qrAdapter
    }


    override fun onTransactionClicked(transaction: TransactionModel) {
        //
    }

    private fun fetchWallet() {
        walletViewModel.fetchWallet(token)
        observeServerResponse(
            walletViewModel.fetchWalletResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.fetchWalletResponse.value?.let {
                if (it.data?.available_balance == null) {
                 //   enterOTPDialog.show()
                } else if (it.data.available_balance >= 0) {
                    binding.walletBalance.text = it.data.available_balance.formatCurrency()
                    binding.walletNumber.text = it.data.phone_no
                }
            }
        }
    }
//    private fun getWalletStatus() {
//    //    walletViewModel.getWalletStatus()
//        observeServerResponse(
//            walletViewModel.getWalletUserResponse,
//            null,
//            requireActivity().supportFragmentManager
//        ) {
//            Log.d("TABRESP", "JUSTCHECKING")
//            walletViewModel.getWalletUserResponse.value?.let {
//                if (it.data?.info?.available_balance == null) {
//                    //   enterOTPDialog.show()
//                } else if (it.data.info.available_balance >= 0) {
//                    binding.walletBalance.text = it.data.info.available_balance.formatCurrency()
//                    binding.walletNumber.text = it.data.info.phone_no
//                }
//                Log.d("TABRESP", it.toString())
//            }
//        }
//    }


    private fun getUserTransactions(recordsNumber: Int) {
        walletViewModel.getUserTransactions(requireContext(), recordsNumber)
        observeServerResponse(
            walletViewModel.getUserTransactionResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.getUserTransactionResponse.value?.let {result ->
                if (result.data?.data.isNullOrEmpty()){
                    binding.noTransactionYet.visibility = View.VISIBLE
               //     binding.myTallyProgressbar.visibility = View.GONE
                    return@observeServerResponse
                }
                result.data?.data.let { resp ->
                    qrDataList = arrayListOf()
                    resp?.forEach {
                        qrDataList.add(it)
                    }
                    tallySetUp()
                }
            }
        }
    }

    override fun onEachTransactionsClicked(data: TallyWalletUserTransactionsResponseItem) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToRecentTransactionDetailsFragment(data)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
       // lifecycle.remov
    }

    fun Fragment.launchWhenResumed(callback: () -> Unit) {
        lifecycleScope.launch {
            lifecycle.withResumed(callback)
        }
    }

    private fun getWalletStatus() {
        walletViewModel.getWalletStatus(requireContext())
        observeServerResponse(
            walletViewModel.getWalletStatusResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.getWalletStatusResponse.value?.let {
                binding.myTallyConstraint.visibility = View.GONE
                binding.notAUserConstraint.visibility = View.VISIBLE
                binding.step1.visibility = View.VISIBLE
                binding.verifyPhoneNumber.visibility = View.VISIBLE
                binding.verifyAccountText.visibility = View.VISIBLE
                binding.step2.visibility = View.GONE
                binding.setPin.visibility = View.GONE
                binding.setPinText.visibility = View.GONE
            }
        }
        observeServerResponse(
            walletViewModel.getWalletUserResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.getWalletUserResponse.value?.let {
                val pinSet = it.data?.info?.pin
                val verified = it.data?.info?.verified
                if (verified!!){
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.GONE
                    binding.verifyPhoneNumber.visibility = View.GONE
                    binding.verifyAccountText.visibility = View.GONE
                }else{
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.VISIBLE
                    binding.verifyPhoneNumber.visibility = View.VISIBLE
                    binding.verifyAccountText.visibility = View.VISIBLE
                }
                if (pinSet!! && verified){
                    binding.myTallyConstraint.visibility = View.VISIBLE
                    binding.walletBalance.text = it.data.info.available_balance.formatCurrency()
                    binding.walletNumber.text = it.data.info.phone_no
                    binding.notAUserConstraint.visibility = View.GONE
                    binding.step1.visibility = View.GONE
                    binding.verifyPhoneNumber.visibility = View.GONE
                    binding.verifyAccountText.visibility = View.GONE
                    binding.step2.visibility = View.GONE
                    binding.setPin.visibility = View.GONE
                    binding.setPinText.visibility = View.GONE
                    getUserTransactions(5)
                    return@observeServerResponse
                }

                if (pinSet && !it.data.info.verified){
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.VISIBLE
                    binding.verifyPhoneNumber.visibility = View.VISIBLE
                    binding.verifyAccountText.visibility = View.VISIBLE
                    binding.step2.visibility = View.GONE
                    binding.setPin.visibility = View.GONE
                    binding.setPinText.visibility = View.GONE
                    return@observeServerResponse
                }

                if (!pinSet && it.data.info.verified){
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.GONE
                    binding.verifyPhoneNumber.visibility = View.GONE
                    binding.verifyAccountText.visibility = View.GONE
                    binding.step2.visibility = View.VISIBLE
                    binding.setPin.visibility = View.VISIBLE
                    binding.setPinText.visibility = View.VISIBLE
                    return@observeServerResponse
                }

                if (!verified){
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.VISIBLE
                    binding.verifyPhoneNumber.visibility = View.VISIBLE
                    binding.verifyAccountText.visibility = View.VISIBLE
                    binding.step2.visibility = View.GONE
                    binding.setPin.visibility = View.GONE
                    binding.setPinText.visibility = View.GONE
                    return@observeServerResponse
                }
                if (!it.data.info.pin){
                    binding.myTallyConstraint.visibility = View.GONE
                    binding.notAUserConstraint.visibility = View.VISIBLE
                    binding.step1.visibility = View.VISIBLE
                    binding.verifyPhoneNumber.visibility = View.VISIBLE
                    binding.verifyAccountText.visibility = View.VISIBLE
                    binding.step2.visibility = View.GONE
                    binding.setPin.visibility = View.GONE
                    binding.setPinText.visibility = View.GONE
                    return@observeServerResponse
                }
                if (it.data.info.available_balance >= 0) {
                    binding.myTallyConstraint.visibility = View.VISIBLE
                    binding.walletBalance.text = it.data.info.available_balance.formatCurrency()
                    binding.walletNumber.text = it.data.info.phone_no
                    return@observeServerResponse
                }
                Log.d("TABRESP", it.toString())
            }
        }
    }

    private fun setTransactionPin(
        transactionPin: String,
        securityQuestionId: String,
        securityQuestion: String,
        securityAnswer: String
    ) {
        walletViewModel.setTransactionPin(requireContext(),
            transactionPin,
            securityQuestionId,
            securityQuestion,
            securityAnswer
        )
        observeServerResponse(
            walletViewModel.setPINResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.setPINResponse.value?.let {
                if (it.data?.status == "success") {
                    getWalletStatus()
                    getUserTransactions(5)
                    //  Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                }
            }

            //sharedPrefsData()
            passwordSetBinding.confirmReprintPasswordEdittext.setText("")
            passwordSetBinding.reprintPasswordEdittext.setText("")
            passwordSetDialog.cancel()
            // walletViewModel.fetchWallet(token)
            walletViewModel.getWalletStatus(requireContext())
        }
    }

    private fun verifyWalletAccount() {
        walletViewModel.verifyWalletNumber(requireContext(), token, true)
        observeServerResponse(
            walletViewModel.verifyWalletResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.verifyWalletResponse.value?.let {
                if (it.data?.verified == 0) {
                    enterOTPDialog.show()
                } else {
                    enterOTPDialog.dismiss()
                    walletViewModel.verifyWalletResponse.removeObservers(viewLifecycleOwner)
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
                    getWalletStatus()
                    showToast(it.data!!.message)
                }
            }
        }
    }


//    @Override
//    override fun onDestroy() {
//        super.onDestroy()
//        if (mySMSBroadcastReceiver != null)
//            activity.unregisterReceiver(mySMSBroadcastReceiver);
//    }
}