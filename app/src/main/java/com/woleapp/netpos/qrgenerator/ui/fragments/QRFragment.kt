package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterOtpBinding
import com.woleapp.netpos.qrgenerator.databinding.NoQrCodeLayoutBinding
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponseItem
import com.woleapp.netpos.qrgenerator.model.wallet.QRTokenResponseItem
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.isDarkModeEnabled
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.WALLET_RESPONSE
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class QRFragment : Fragment(), QrAdapter.OnQrClick {
    private lateinit var _binding: FragmentQRBinding
    private val binding get() = _binding
    private lateinit var qrAdapter: QrAdapter
    private lateinit var qrDataList: ArrayList<FetchQrTokenResponseItem>
    private val qrViewModel by activityViewModels<QRViewModel>()
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var token: String
    private lateinit var loader: android.app.AlertDialog
    private var tallyWalletBalance: Int = 0
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var otp: String
 //   lateinit var dataPasser: VerificationInterface


    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    @Named("io-scheduler")
    lateinit var ioScheduler: Scheduler

    @Inject
    @Named("main-scheduler")
    lateinit var mainThreadScheduler: Scheduler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQRBinding.inflate(inflater, container, false)
        if (BuildConfig.FLAVOR.contains("qrgenerator")) {
            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        activity?.finishAffinity()
                    }
                })
        }else{
            binding.verifyAccount.visibility = View.GONE
        }
        val header = Singletons().getTallyUserToken()!!
        token = "Bearer $header"
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)

        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@QRFragment
            executePendingBindings()
        }
        enterOTPDialog = AlertDialog.Builder(requireContext())
            .setView(enterOTPBinding.root)
            .setCancelable(false)
            .create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyAccount.setOnClickListener {
            verifyWalletAccount()
        }
        walletViewModel.fetchWalletMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
//        observeServerResponse(
//            walletViewModel.verifyWalletResponse,
//            loader,
//            requireActivity().supportFragmentManager
//        ) {
//            walletViewModel.verifyWalletResponse.value?.let {
//                if (it.data!!.verified == 0){
//                    enterOTPDialog.show()
//                }else{
//                    enterOTPDialog.dismiss()
//                }
//            }
//        }
        qrDataList = walletViewModel.listOfQrTokens
        getQrCodes()

        binding.button.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }


        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@QRFragment
            executePendingBindings()
        }
        enterOTPDialog = AlertDialog.Builder(requireContext())
            .setView(enterOTPBinding.root)
            .setCancelable(false)
            .create()

        //fetchWallet()

        enterOTPBinding.proceed.setOnClickListener {
            otp = enterOTPBinding.otpEdittext.text?.trim().toString()
            if (otp.isEmpty()) {
                showToast("Please enter OTP")
                return@setOnClickListener
            }
            verifyWalletOTP(token, otp)
        }
        enterOTPBinding.resendOtp.setOnClickListener {
            verifyWalletAccount()
        }
        enterOTPBinding.closeDialog.setOnClickListener {
            enterOTPDialog.dismiss()
            verifyWalletOTP(token, "")
        }

        binding.generateMoreQr.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }

    }

    private fun qrSetUp() {
        qrAdapter = QrAdapter(qrDataList, this)
        binding.qrRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.qrRecycler.adapter = qrAdapter
    }

    private fun getQrCodes() {
        loader.show()
        observeServerResponse(
            walletViewModel.fetchQrToken(token),
            loader,
            compositeDisposable,
            ioScheduler,
            mainThreadScheduler,
        ) {

            if (qrDataList.isNullOrEmpty()){
                binding.noQrCodesLayout.visibility = View.VISIBLE
            }else{
                binding.noQrCodesLayout.visibility = View.GONE
                qrSetUp()
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
                    val verified = it.data?.verified!!
                    if (verified == 0){
                        binding.verifyAccount.visibility = View.VISIBLE
                    }else{
                        binding.verifyAccount.visibility = View.GONE
                    }
                    walletViewModel.fetchWalletResponse.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }
    private fun verifyWalletAccount() {
        walletViewModel.verifyWalletNumber(token, true)
        observeServerResponse(
            walletViewModel.verifyWalletResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.verifyWalletResponse.value?.let {
                if (it.data?.verified == 0){
                    enterOTPDialog.show()
                }else{
                    enterOTPDialog.dismiss()
                    walletViewModel.verifyWalletResponse.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }


    override fun viewTransaction(qrModel: FetchQrTokenResponseItem) {
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToQrDetailsFragment2(qrModel)
        findNavController().navigate(action)
    }

    override fun onViewQr(qrModel: FetchQrTokenResponseItem) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToDisplayQrFragment23(qrModel)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        walletViewModel.listOfQrTokens.removeAll(qrDataList)
      //  qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}
