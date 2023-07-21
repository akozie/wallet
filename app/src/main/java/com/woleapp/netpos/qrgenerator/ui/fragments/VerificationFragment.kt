package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.SecurityQuestionsAdapter
import com.woleapp.netpos.qrgenerator.databinding.*
import com.woleapp.netpos.qrgenerator.model.login.UserEntity
import com.woleapp.netpos.qrgenerator.model.login.UserViewModel
import com.woleapp.netpos.qrgenerator.model.wallet.NewGetSecurityQuestionResponseItem
import com.woleapp.netpos.qrgenerator.utils.PIN_PASSWORD
import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel

class VerificationFragment : Fragment() {

    private lateinit var binding: FragmentVerificationBinding
    private lateinit var otp: String
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var token: String
    private lateinit var loader: android.app.AlertDialog
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var passwordSetDialog: AlertDialog
    private lateinit var passwordSetBinding: LayoutSetTransactionPinPrefTextBinding
    private lateinit var getSecurityQuestion: AutoCompleteTextView
    private lateinit var securityQuestionAnswer: String
    private lateinit var listOfQuestions: NewGetSecurityQuestionResponseItem
    private var transactionCheckbox = false
    private lateinit var updatePinDialog: AlertDialog
    private lateinit var updatePinBinding: LayoutUpdatePinPrefTextBinding
    private var securityQuestion: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_verification, container, false)

        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@VerificationFragment
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
            lifecycleOwner = this@VerificationFragment
            executePendingBindings()
        }

        passwordSetDialog =
            AlertDialog.Builder(requireContext())
                .setView(passwordSetBinding.root)
                .setCancelable(false)
                .create()

        updatePinBinding =
            LayoutUpdatePinPrefTextBinding.inflate(
                LayoutInflater.from(requireContext()),
                null,
                false
            )
                .apply {
                    lifecycleOwner = this@VerificationFragment
                    executePendingBindings()
                }


        updatePinDialog =
            AlertDialog.Builder(requireContext())
                .setView(updatePinBinding.root)
                .setCancelable(false)
                .create()

        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        val header = Singletons().getTallyUserToken()!!
        token = "Bearer $header"
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        passwordSetBinding.cancelDialog.setOnClickListener {
            passwordSetDialog.dismiss()
        }

        binding.verifyPhoneNumber.setOnClickListener {
            verifyWalletAccount()
        }


        binding.setSecurityQuestion.setOnClickListener {
            passwordSetDialog.show()
        }

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
        getSecurityQuestion = passwordSetBinding.securityQuestions


        val securityQuestionAdapter = SecurityQuestionsAdapter(
            RandomUtils.displaySecurityQuestion(), requireContext(),
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
        passwordSetBinding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Handle checkbox state changes here
            transactionCheckbox = isChecked
        }

        //Update PIN
        updatePinBinding.closeDialog.setOnClickListener {
            updatePinDialog.dismiss()
        }

        updatePinBinding.pinUpdateButton.setOnClickListener {
            val selectedSecurityQuestion = securityQuestion
            val securityAnswer = updatePinBinding.securityAnswers.text.toString()
            val otp = updatePinBinding.transactionOtp.text.toString()
            val newPin = updatePinBinding.transactionPinEdittext.text.toString()
            val confirmNewPin = updatePinBinding.confirmPinEdittext.text.toString()

            if (securityAnswer.isEmpty()) {
                showToast("Please answer your security question")
                return@setOnClickListener
            }
            if (otp.isEmpty()) {
                showToast("OTP cannot be empty")
                return@setOnClickListener
            }
            if (newPin.isEmpty()) {
                showToast("Please enter your new PIN")
                return@setOnClickListener
            }
            if (newPin.length < 4) {
                showToast("The transaction pin must not be less than 4")
                return@setOnClickListener
            }
            if (confirmNewPin.isEmpty()) {
                showToast("Please confirm your new PIN")
            }
            if (newPin != confirmNewPin) {
                showToast("PIN do not match")
                return@setOnClickListener
            }
            if (selectedSecurityQuestion != null) {
                updateTransactionPin(
                    newPin,
                    otp,
                    securityAnswer,
                    selectedSecurityQuestion.toString()
                )
            }
        }

            getWalletStatus()

        binding.updatePin.setOnClickListener {
            getOtpVerificationToUpdatePin()
            getSelectedQuestion()
            updatePinDialog.show()
        }
//        passwordDialogBinding.update.setOnClickListener {
//            getOtpVerificationToUpdatePin()
//            getSelectedQuestion()
//            updatePinDialog.show()
//            inputPasswordDialog.dismiss()
//        }
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
                    showToast(it.data!!.message)
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
                if (it.data?.verified == 0) {
                    enterOTPDialog.show()
                } else {
                    enterOTPDialog.dismiss()
                    walletViewModel.verifyWalletResponse.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }

    private fun setTransactionPin(
        transactionPin: String,
        securityQuestionId: String,
        securityQuestion: String,
        securityAnswer: String
    ) {
        walletViewModel.setTransactionPin(
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
                    //  Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                }
            }

            //sharedPrefsData()
            passwordSetBinding.confirmReprintPasswordEdittext.setText("")
            passwordSetBinding.reprintPasswordEdittext.setText("")
            passwordSetDialog.cancel()
           // walletViewModel.fetchWallet(token)
            walletViewModel.getWalletStatus()
        }
    }

    private fun fetchWallet() {
        walletViewModel.fetchWallet(token)
        observeServerResponse(
            walletViewModel.fetchWalletResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.fetchWalletResponse.value?.let {
                if (it.data?.verified == 0){
                    //showToast("Source User Wallet verification required")
                    binding.updatePin.visibility = View.GONE
                }else{
                    binding.verifyPhoneNumber.visibility = View.GONE
                    binding.verifyAccountText.visibility = View.GONE
                    binding.setSecurityQuestion.visibility = View.GONE
                    binding.securityQuestionsText.visibility = View.GONE
                }
            }
        }
    }
    private fun getWalletStatus() {
        //    walletViewModel.getWalletStatus()
        observeServerResponse(
            walletViewModel.getWalletUserResponse,
            null,
            requireActivity().supportFragmentManager
        ) {
            Log.d("TABRESP", "JUSTCHECKING")
            walletViewModel.getWalletUserResponse.value?.let {
                if (it.data?.info?.verified == false){
                    //showToast("Source User Wallet verification required")
                    binding.updatePin.visibility = View.GONE
                }else{
                    if (it.data?.info?.pin == false){
                        binding.setSecurityQuestion.visibility = View.VISIBLE
                        return@observeServerResponse
                    }
                    binding.verifyPhoneNumber.visibility = View.GONE
                    binding.verifyAccountText.visibility = View.GONE
                    binding.setSecurityQuestion.visibility = View.GONE
                    binding.securityQuestionsText.visibility = View.GONE
                }
            }
        }
    }

    private fun updateTransactionPin(
        newPin: String,
        otp: String,
        securityAnswer: String,
        securityQuestion: String
    ) {
        walletViewModel.updateTransactionPin(newPin, otp, securityAnswer, securityQuestion)
        observeServerResponse(
            walletViewModel.updatePinResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            val email = Singletons().getCurrentlyLoggedInUser()?.email
            email?.let {
                userViewModel.updatePin(UserEntity(it, newPin))
            }
            updatePinBinding.confirmPinEdittext.setText("")
            updatePinBinding.transactionPinEdittext.setText("")
            updatePinDialog.cancel()
        }
    }

    private fun getSelectedQuestion(
    ) {
        walletViewModel.getSelectedQuestion()
        observeServerResponse(
            walletViewModel.getSelectedQuestionResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            val selectedQuestion = walletViewModel.getSelectedQuestionResponse.value?.data
            selectedQuestion?.let {
                updatePinBinding.securityQuestions.setText(it.question)
                securityQuestion = it.question_id
            }
        }
    }

    private fun getOtpVerificationToUpdatePin() {
        walletViewModel.getOtpVerificationToUpdatePin()
        observeServerResponse(
            walletViewModel.getOtpToUpdatePinResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            val otpResponse = walletViewModel.getOtpToUpdatePinResponse.value
            otpResponse?.let {
                showToast(otpResponse.data!!.message)
            }
        }
    }

}