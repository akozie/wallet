package com.woleapp.netpos.qrgenerator.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterOtpBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutNoInternetBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.launchWhenResumed
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponseForQrFragment
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.TransactionViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates


@AndroidEntryPoint
class QRFragment : Fragment(), QrAdapter.OnQrClick {
    private var _binding: FragmentQRBinding? = null
    private val binding get() = _binding!!
    private lateinit var qrAdapter: QrAdapter

    // private lateinit var qrDataList: ArrayList<FetchQrTokenResponseItem>
    private lateinit var qrDataList: ArrayList<GenerateQRResponse>
    private val qrViewModel by activityViewModels<QRViewModel>()
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private val transactionViewModel by activityViewModels<TransactionViewModel>()
    private lateinit var token: String
    private lateinit var loader: android.app.AlertDialog
    private var tallyWalletBalance: Int = 0
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var otp: String
    private var userId by Delegates.notNull<Int>()

    //   lateinit var dataPasser: VerificationInterface
    private lateinit var noInternetDialog: AlertDialog
    private lateinit var noInternetBinding: LayoutNoInternetBinding
    private lateinit var networkConnectivityHelper: NetworkConnectivityHelper

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
        } else {
            binding.verifyAccount.visibility = View.GONE
        }
        networkConnectivityHelper = NetworkConnectivityHelper(requireActivity())
        val header = Singletons().getTallyUserToken(requireContext())!!
        token = "Bearer $header"
        loader = alertDialog(requireContext())
        userId = Singletons().getCurrentlyLoggedInUser(requireContext())?.id!!
        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
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
        qrDataList = arrayListOf()
        binding.verifyAccount.setOnClickListener {
            verifyWalletAccount()
        }


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
            lifecycleOwner = viewLifecycleOwner
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



        noInternetBinding = LayoutNoInternetBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@QRFragment
            executePendingBindings()
        }
        noInternetDialog = AlertDialog.Builder(requireContext())
            .setView(noInternetBinding.root)
            .setCancelable(false)
            .create()
        networkConnectivityHelper.observeNetworkStatus()
            .subscribe {
                if (it) {
                    requireActivity().runOnUiThread(Runnable {
                        // Stuff that updates the UI
                        noInternetDialog.dismiss()
                    })
                    launchWhenResumed {
                        // Do something
                        //    qrViewModel.fetchQrToken(requireContext())
                    }
                } else {
                    requireActivity().runOnUiThread(Runnable {
                        // Stuff that updates the UI
                        noInternetDialog.show()
                    })
                }
            }
            .isDisposed

        transactionViewModel.getQRFromDb(userId.toString())

    }


    override fun onResume() {
        super.onResume()
        getQrCodes()
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                val contacts = mutableListOf<String>()
//
//                if (data.data != null) {
//                    // Single contact selected
//                    val contactUri: Uri = data.data!!
//                    val cursor: Cursor? = requireActivity().contentResolver.query(
//                        contactUri, null, null, null, null
//                    )
//                    cursor?.use {
//                        if (it.moveToFirst()) {
//                            val contactName: String =
//                                it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                            contacts.add(contactName)
//                            Log.d("CONTACTNAME", contactName)
//                        }
//                    }
//                } else {
//                    // Multiple contacts selected
//                    val cursor: Cursor? = requireActivity().contentResolver.query(
//                        data.data!!, null, null, null, null
//                    )
//                    cursor?.use {
//                        while (it.moveToNext()) {
//                            val contactName: String =
//                                it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                            contacts.add(contactName)
//                            Log.d("MULTIPLECONTACTNAME", contactName)
//                        }
//                    }
//                }
//
//                // Do something with the selected contacts (e.g., display or process them)
//            }
//        }
//    }

    private fun qrSetUp() {
        qrAdapter = QrAdapter(qrDataList, this)
        binding.qrRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.qrRecycler.adapter = qrAdapter
        qrAdapter.notifyDataChanged(qrDataList)
    }

    private fun getQrCodes() {
        observeServerResponseForQrFragment(
            transactionViewModel.getQRFromDbResponse,
            loader,
            requireActivity().supportFragmentManager,
        ) {
            qrDataList = it as ArrayList<GenerateQRResponse>
            //  showToast(qrDataList.size.toString())
            if (qrDataList.isEmpty()) {
                binding.noQrCodesLayout.visibility = View.VISIBLE
            } else {
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
                    if (verified == 0) {
                        binding.verifyAccount.visibility = View.VISIBLE
                    } else {
                        binding.verifyAccount.visibility = View.GONE
                    }
                    walletViewModel.fetchWalletResponse.removeObservers(viewLifecycleOwner)
                }
            }
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


    override fun viewTransaction(qrModel: GenerateQRResponse) {
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToQrDetailsFragment2(qrModel)
        findNavController().navigate(action)
    }

    override fun onViewQr(qrModel: GenerateQRResponse) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToDisplayQrFragment23(qrModel)
        findNavController().navigate(action)
    }

}
