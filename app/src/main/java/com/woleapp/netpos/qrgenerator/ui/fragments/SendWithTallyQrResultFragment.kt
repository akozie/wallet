package com.woleapp.netpos.qrgenerator.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyQrResultBinding
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SendWithTallyQrResultFragment : Fragment() {

    private lateinit var binding: FragmentSendWithTallyQrResultBinding
    private lateinit var amount: TextView
    private lateinit var walletNumber: TextInputEditText
    private lateinit var loader: AlertDialog
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private var verified = false
    private lateinit var networkConnectivityHelper: NetworkConnectivityHelper

    //  private lateinit var connectivity: Connectivity
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_send_with_tally_qr_result,
            container,
            false
        )
        networkConnectivityHelper = NetworkConnectivityHelper(requireContext())
        //    connectivity = Connectivity(requireContext())
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Singletons().getTallyWalletBalance(requireContext())?.info?.available_balance?.let {
            binding.availableBalance.text = it.formatCurrency()
        }

        loader = alertDialog(requireContext())
        initViews()
        val creditWalletNumber = Singletons().getAmountAndTallyNumber(requireContext())?.tallyNumber
        walletNumber.setText(creditWalletNumber)

        Singletons().getTallyWalletBalance(requireContext())?.info?.verified?.let {
            verified = it
        }

        binding.btnProcessWalletTransfer.setOnClickListener {
            if (binding.enterDescAccount.text?.trim().toString().isEmpty()) {
                showToast("Please enter destination account")
                return@setOnClickListener
            }
//            if (binding.enterDescAccount.text?.trim().toString().length < 11) {
//                showToast("The destination account must not be less than 11")
//                return@setOnClickListener
//            }
            if (binding.tallyQrAmount.text?.trim().toString().isEmpty()) {
                showToast("Please enter wallet amount")
                return@setOnClickListener
            }
            if (binding.enterTransactionPin.text?.trim().toString().isEmpty()) {
                showToast("Please enter transaction PIN")
                return@setOnClickListener
            }
            if (binding.tallyQrAmount.text?.trim().toString().toInt() < 100) {
                showToast("The transaction amount must be at least 100 naira")
                return@setOnClickListener
            }
            if (binding.enterTransactionPin.text?.trim().toString().length < 4) {
                showToast("The transaction pin must not be less than 4")
                return@setOnClickListener
            }
            if (!verified) {
                showToast("Please you need to verify your number")
                return@setOnClickListener
            }
            sendWithTallyNumber()

//                if (isOnline(requireContext())) {
//                    sendWithTallyNumber()
//                } else {
//                    showToast("This device is not connected to the internet")
//                }
        }
    }

    private fun initViews() {
        with(binding) {
            amount = tallyQrAmount
            walletNumber = enterDescAccount
        }
    }

    private fun sendWithTallyNumber() {
        loader.show()
        val sendWithTallyNumberRequest = SendWithTallyNumberRequest(
            dest_account = walletNumber.text?.trim().toString(),
            transaction_amount = amount.text.trim().toString(),
            transaction_pin = binding.enterTransactionPin.text?.trim().toString(),
            adminAccessToken = Singletons().getAdminAccessToken(requireContext())!!,
            userTokenId = Singletons().getWalletUserTokenId(requireContext())!!,
            accountId = Singletons().getAccountId(requireContext())!!
        )
        observeServerResponse(
            walletViewModel.sendWithTallyNumber(
                requireContext(),
                "Bearer ${Singletons().getTallyUserToken(requireContext())!!}",
                sendWithTallyNumberRequest
            ),
            loader,
            compositeDisposable,
            ioScheduler,
            mainThreadScheduler,
        ) {
            val walletResponse = EncryptedPrefsUtils.getString(requireContext(), WALLET_RESPONSE)
            showToast(walletResponse.toString())
            findNavController().popBackStack()
        }
    }
}