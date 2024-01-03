package com.woleapp.netpos.qrgenerator.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.FrequentBeneficiariesAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyNumberBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutCustomLoadingDialogBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutShareTallyBinding
import com.woleapp.netpos.qrgenerator.databinding.SuccessLayoutBinding
import com.woleapp.netpos.qrgenerator.model.FrequentBeneficiariesModel
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SendWithTallyNumberFragment : Fragment() {

    private lateinit var binding: FragmentSendWithTallyNumberBinding
    private lateinit var frequentBeneficiariesAdapter: FrequentBeneficiariesAdapter
    private lateinit var frequentBeneficiariesList: ArrayList<FrequentBeneficiariesModel>
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: AlertDialog
    private lateinit var transactionStatusDialog: AlertDialog
    private lateinit var transactionStatusBinding: SuccessLayoutBinding
    private lateinit var inviteToTallyDialog: AlertDialog
    private lateinit var inviteToTallyBinding: LayoutShareTallyBinding
    private lateinit var customLayout: LayoutCustomLoadingDialogBinding
    private var verified = false
    private var timeSeconds = 5L
    private var balanceResult = String()


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

        inviteToTallyBinding = LayoutShareTallyBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@SendWithTallyNumberFragment
            executePendingBindings()
        }

        inviteToTallyDialog = AlertDialog.Builder(requireContext())
            .setView(inviteToTallyBinding.root)
            .create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = alertDialog(requireContext())
        beneficiariesList()
        qrSetUp()
        RandomUtils.fetchWalletMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                if (message == "Recipient Wallet does not exist") {
                    inviteToTallyBinding.textWrapper.text = message
                    inviteToTallyDialog.show()
                    //   showToast(message)
                } else {
                    showToast(message)
                }
            }
        }

        inviteToTallyBinding.share.setOnClickListener {
            genericPermissionHandler(
                this,
                requireContext(),
                Manifest.permission.READ_CONTACTS,
                READ_CONTACTS_PERMISSION_REQUEST_CODE,
                "You need to grant this permission to share contacts",
            ) {
                inviteToTallyDialog.dismiss()
                val action =
                    SendWithTallyNumberFragmentDirections.actionSendWithTallyNumberFragmentToContactsFragment()
                findNavController().navigate(action)
            }
        }


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
            if (binding.enterWalletAmount.text?.trim().toString().isEmpty()) {
                showToast("Please enter wallet amount")
                return@setOnClickListener
            }
            if (binding.enterWalletAmount.text?.trim().toString().toInt() < 100) {
                showToast("The transaction amount must be at least 100 naira")
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
            sendWithTallyNumber()
            // startResendTimer()
        }


        // uncomment later
//        Singletons().getTallyWalletBalance(requireContext())?.info?.available_balance?.let {
//            binding.availableBalance.text = it.formatCurrency()
//        }

        Singletons().getTallyWalletBalanceTest(requireContext())?.let {
            binding.availableBalance.text = it
        }

        transactionStatusBinding.cancelButton.setOnClickListener {
            transactionStatusDialog.dismiss()
            val action = SendWithTallyNumberFragmentDirections.actionSendWithTallyNumberFragmentToTransactionsFragment()
            findNavController().navigate(action)
        }
        transactionStatusBinding.viewGeneratedQr.setOnClickListener {
            transactionStatusDialog.dismiss()
            val action = SendWithTallyNumberFragmentDirections.actionSendWithTallyNumberFragmentToTransactionsFragment()
            findNavController().navigate(action)
        }

        transactionStatusBinding.printReceipt.visibility = View.GONE
        transactionStatusBinding.viewGeneratedQr.visibility = View.VISIBLE
        transactionStatusBinding.statusIconLAV.setAnimation(R.raw.lottiesuccess)
        transactionStatusBinding.successFailed.text = getString(R.string.success)
        transactionStatusBinding.viewGeneratedQr.text = "OK"
        transactionStatusBinding.successFailed.setTextColor(resources.getColor(R.color.success))
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

    private fun startResendTimer() {
        val timer = Timer()

        // Schedule a task to run repeatedly at a fixed rate
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Code to run repeatedly at a fixed rate
                timeSeconds--
                activity?.runOnUiThread {
                    loader.show()
                }
                if (timeSeconds <= 0) {
                    timeSeconds = 5L
                    timer.cancel()
                    activity?.runOnUiThread {
                        loader.dismiss()
                        balanceResult = (binding.availableBalance.text.toString().substring(1, binding.availableBalance.text.toString().length-3).replace("," ,"").toInt() - binding.enterWalletAmount.text.toString().toInt()).toString()
                        binding.availableBalance.text = balanceResult.toInt().formatCurrency()
                        EncryptedPrefsUtils.putString(requireContext(), PREF_TALLY_WALLET_TEST, balanceResult.toInt().formatCurrency())
                        transactionStatusDialog.show()
                        transactionStatusBinding.qrAmount.text = binding.enterWalletAmount.text.toString().toLong().formatCurrency("\u20A6")
                    }
                }
            }
        }, 0, 1000) // run 1000 milliseconds (1 second)
    }

}