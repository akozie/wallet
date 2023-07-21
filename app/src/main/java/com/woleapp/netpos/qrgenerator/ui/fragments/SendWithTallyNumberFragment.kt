package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.FrequentBeneficiariesAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyNumberBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutCustomLoadingDialogBinding
import com.woleapp.netpos.qrgenerator.databinding.SuccessLayoutBinding
import com.woleapp.netpos.qrgenerator.model.FrequentBeneficiariesModel
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.isOnline
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SendWithTallyNumberFragment : Fragment() {

    private lateinit var binding: FragmentSendWithTallyNumberBinding
    private lateinit var frequentBeneficiariesAdapter: FrequentBeneficiariesAdapter
    private lateinit var frequentBeneficiariesList: ArrayList<FrequentBeneficiariesModel>
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: AlertDialog
    private lateinit var transactionStatusDialog: AlertDialog
    private lateinit var transactionStatusBinding: SuccessLayoutBinding
    private lateinit var customLayout: LayoutCustomLoadingDialogBinding
    private var verified = false


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
            R.layout.fragment_send_with_tally_number,
            container,
            false
        )
        customLayout = LayoutCustomLoadingDialogBinding.inflate(layoutInflater)

        transactionStatusBinding = SuccessLayoutBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@SendWithTallyNumberFragment
            executePendingBindings()
        }
        transactionStatusDialog = AlertDialog.Builder(requireContext())
            .setView(transactionStatusBinding.root)
            .setCancelable(true)
            .create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = alertDialog(requireContext())
        beneficiariesList()
        qrSetUp()
        walletViewModel.fetchWalletMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        Singletons().getTallyWalletBalance()?.info?.verified?.let {
            verified = it
        }
        binding.btnProcessWalletTransfer.setOnClickListener {
            if (binding.enterDescAccount.text?.trim().toString().isEmpty()) {
                showToast("Please enter destination account")
                return@setOnClickListener
            }
            if (binding.enterDescAccount.text?.trim().toString().length < 11) {
                showToast("The destination account must not be less than 11")
                return@setOnClickListener
            }
            if (binding.enterWalletAmount.text?.trim().toString().isEmpty()) {
                showToast("Please enter wallet amount")
                return@setOnClickListener
            }
            if (binding.enterTransactionPin.text?.trim().toString().isEmpty()) {
                showToast("Please enter transaction PIN")
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
                if (isOnline(requireContext())) {
                    sendWithTallyNumber()
                } else {
                    showToast("This device is not connected to the internet")
                }
        }

        Singletons().getTallyWalletBalance()?.info?.available_balance?.let {
            binding.availableBalance.text = it.formatCurrency()
        }

        transactionStatusBinding.home.setOnClickListener {
            transactionStatusDialog.dismiss()
        }
    }

    private fun qrSetUp() {
        frequentBeneficiariesAdapter = FrequentBeneficiariesAdapter(frequentBeneficiariesList)
        binding.beneficiariesRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.beneficiariesRecyclerview.adapter = frequentBeneficiariesAdapter
    }

    private fun beneficiariesList() {
        frequentBeneficiariesList = arrayListOf()
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
        frequentBeneficiariesList.add(FrequentBeneficiariesModel("", "Toyosi Idowu"))
    }

    private fun sendWithTallyNumber() {
        loader.show()
        val sendWithTallyNumberRequest = SendWithTallyNumberRequest(
            dest_account = binding.enterDescAccount.text?.trim().toString(),
            transaction_amount = binding.enterWalletAmount.text?.trim().toString(),
            transaction_pin = binding.enterTransactionPin.text?.trim().toString()
        )
        observeServerResponse(
            walletViewModel.sendWithTallyNumber(
                requireContext(),
                "Bearer ${Singletons().getTallyUserToken()!!}",
                sendWithTallyNumberRequest
            ),
            loader,
            compositeDisposable,
            ioScheduler,
            mainThreadScheduler,
        ) {
            val walletResponse = Prefs.getString(WALLET_RESPONSE, "")
            showToast(walletResponse)
            findNavController().popBackStack()
        }
    }
}