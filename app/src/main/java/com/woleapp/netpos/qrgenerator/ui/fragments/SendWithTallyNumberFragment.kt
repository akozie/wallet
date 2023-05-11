package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.FrequentBeneficiariesAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyNumberBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterOtpBinding
import com.woleapp.netpos.qrgenerator.databinding.SuccessLayoutBinding
import com.woleapp.netpos.qrgenerator.model.FrequentBeneficiariesModel
import com.woleapp.netpos.qrgenerator.model.pay.ModalData
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.IoScheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SendWithTallyNumberFragment : Fragment() {

    private lateinit var binding: FragmentSendWithTallyNumberBinding
    private lateinit var frequentBeneficiariesAdapter: FrequentBeneficiariesAdapter
    private lateinit var frequentBeneficiariesList: ArrayList<FrequentBeneficiariesModel>
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: AlertDialog
    private var modalData: ModalData? = null
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var transactionStatusDialog: AlertDialog
    private lateinit var transactionStatusBinding: SuccessLayoutBinding

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

        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@SendWithTallyNumberFragment
            executePendingBindings()
        }
        enterOTPDialog = AlertDialog.Builder(requireContext())
            .setView(enterOTPBinding.root)
            .setCancelable(true)
            .create()

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
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        beneficiariesList()
        qrSetUp()
        initViews()
        binding.btnProcessWalletTransfer.setOnClickListener {
            sendWithTallyNumber()
        }

        Singletons().getTallyWalletBalance()?.balance?.let {
            binding.availableBalance.text = it.formatCurrency()
        }

        binding.setTransactionPin.setOnClickListener {
            enterOTPDialog.show()
        }
        enterOTPBinding.proceed.setOnClickListener {
            val transactionPin = enterOTPBinding.otpEdittext.text?.trim().toString()
            setTransactionPin(transactionPin)
        }
        transactionStatusBinding.home.setOnClickListener {
            transactionStatusDialog.dismiss()
        }
    }

    private fun initViews() {
        enterOTPBinding.passwordWrapper.hint = "Enter PIN"
        enterOTPBinding.proceed.setText("SET PIN")
        enterOTPBinding.otpEdittext.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    //        private fun findNavController(): NavController? {
//        val navHostFragment = (requireActivity() as? MainActivity)?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
//        return navHostFragment?.navController
//    }
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
            walletViewModel.sendWithTallyNumber("Bearer ${Singletons().getTallyUserToken()!!}",sendWithTallyNumberRequest),
            loader,
            compositeDisposable,
            ioScheduler,
            mainThreadScheduler,
        ) {
            activity?.supportFragmentManager?.popBackStack()
            showToast("YEEEEE")
        }
    }


    private fun setTransactionPin(transactionPin: String) {
        walletViewModel.setTransactionPin(transactionPin)
        observeServerResponse(
            walletViewModel.setPINResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.setPINResponse.value?.let {
                if (it.data?.status == "success") {
                    showToast(it.data.message)
                    enterOTPDialog.dismiss()
                }
            }
        }
    }
}