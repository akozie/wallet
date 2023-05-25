package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyQrBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyQrResultBinding
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.showToast
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_send_with_tally_qr_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletViewModel.fetchWalletMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        initViews()
        val creditAmount = Singletons().getAmountAndTallyNumber()?.amount
        val creditWalletNumber = Singletons().getAmountAndTallyNumber()?.tallyNumber
        amount.text = creditAmount
        walletNumber.setText(creditWalletNumber)

        binding.btnGenerateQr.setOnClickListener {
            sendWithTallyNumber()
        }
    }

    private fun initViews() {
        with(binding){
            amount = tallyQrAmount
            walletNumber = tallyQrNumber
        }
    }

    private fun sendWithTallyNumber() {
        loader.show()
        val sendWithTallyNumberRequest = SendWithTallyNumberRequest(
            dest_account = walletNumber.text?.trim().toString(),
            transaction_amount = amount.text.trim().toString(),
            transaction_pin = binding.pin.text?.trim().toString()
        )
        observeServerResponse(
            walletViewModel.sendWithTallyNumber("Bearer ${Singletons().getTallyUserToken()!!}",sendWithTallyNumberRequest),
            loader,
            compositeDisposable,
            ioScheduler,
            mainThreadScheduler,
        ) {
            findNavController().popBackStack()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.mainActivityfragmentContainerView, MyTallyFragment())
//                .addToBackStack(null)
//                .commit()
        }
    }
}