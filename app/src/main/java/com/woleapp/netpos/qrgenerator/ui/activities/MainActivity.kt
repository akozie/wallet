package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.SecurityQuestionsAdapter
import com.woleapp.netpos.qrgenerator.databinding.ActivityMainBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterPasswordBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutReprintPasswordPrefTextBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutUpdatePinPrefTextBinding
import com.woleapp.netpos.qrgenerator.model.wallet.GetSecurityQuestionResponseItem
import com.woleapp.netpos.qrgenerator.utils.PIN_PASSWORD
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponseActivity
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val qrViewModel by viewModels<WalletViewModel>()
    private lateinit var inputPasswordDialog: AlertDialog
    private lateinit var passwordDialogBinding: LayoutEnterPasswordBinding
    private lateinit var passwordSetDialog: AlertDialog
    private lateinit var passwordSetBinding: LayoutReprintPasswordPrefTextBinding
    private lateinit var updatePinDialog: AlertDialog
    private lateinit var updatePinBinding: LayoutUpdatePinPrefTextBinding
    private lateinit var getSecurityQuestion: AutoCompleteTextView
    private lateinit var securityQuestionAnswer: String
    private lateinit var loader: android.app.AlertDialog
    private var securityQuestion: Int? = 0
    private lateinit var listOfQuestions: GetSecurityQuestionResponseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loader = alertDialog(this, R.layout.layout_loading_dialog)
        qrViewModel.getSecurityQuestions()

        qrViewModel.fetchWalletMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

//        val sendWithTallyQrFragment = SendWithTallyQrFragment()
//        binding.scanWithTallyQr.setOnClickListener {
//            if (sendWithTallyQrFragment.isAdded) {
//                return@setOnClickListener
//            }
//            navigateToDestination(sendWithTallyQrFragment)
//        }
//
//        val sendWithTallyNumber = SendWithTallyNumberFragment()
//        binding.sendWithTallyNumber.setOnClickListener {
//            if (sendWithTallyNumber.isAdded) {
//                return@setOnClickListener
//            }
//            navigateToDestination(sendWithTallyNumber)
//        }
//
//        val requestPayment = RequestPaymentFragment()
//        binding.requestPayment.setOnClickListener {
//            if (requestPayment.isAdded) {
//                return@setOnClickListener
//            }
//            navigateToDestination(requestPayment)
//        }

        passwordDialogBinding =
            LayoutEnterPasswordBinding.inflate(LayoutInflater.from(this), null, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    executePendingBindings()
                }
        updatePinBinding =
            LayoutUpdatePinPrefTextBinding.inflate(LayoutInflater.from(this), null, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    executePendingBindings()
                }
        passwordSetBinding = LayoutReprintPasswordPrefTextBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        ).apply {
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }

        updatePinBinding.pinUpdateButton.setOnClickListener {
            val selectedSecurityQuestion = securityQuestion
            val securityAnswer = updatePinBinding.securityAnswers.text.toString()
            val otp = updatePinBinding.transactionOtp.text.toString()
            val newPin = updatePinBinding.transactionPinEdittext.text.toString()
            val confirmNewPin = updatePinBinding.confirmPinEdittext.text.toString()

            if (securityAnswer.isEmpty()) {
                Toast.makeText(this, "Please answer your security question", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (otp.isEmpty()) {
                Toast.makeText(this, "OTP cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPin.isEmpty()) {
                Toast.makeText(this, "Please enter your new PIN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (confirmNewPin.isEmpty()) {
                Toast.makeText(this, "Please confirm your new PIN", Toast.LENGTH_SHORT).show()
            }

            if (newPin != confirmNewPin) {
                Toast.makeText(this, "PIN do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedSecurityQuestion != null) {
                updateTransactionPin(newPin, otp, securityAnswer, selectedSecurityQuestion)
            }
        }

        passwordSetBinding.save.setOnClickListener {
            val transactionPIN = passwordSetBinding.reprintPasswordEdittext.text.toString()
            val securityQuestion = passwordSetBinding.securityQuestions.text.toString()
            val securityAnswer = passwordSetBinding.securityAnswers.text.toString()
            val questionID = listOfQuestions.id
            if (transactionPIN.isEmpty()) {
                Toast.makeText(this, "PIN cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (securityAnswer.isEmpty()) {
                Toast.makeText(this, "Please answer your security question", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (transactionPIN != passwordSetBinding.confirmReprintPasswordEdittext.text.toString()) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            setTransactionPin(transactionPIN, questionID.toString(), securityQuestion, securityAnswer)
        }

        getSecurityQuestion = passwordSetBinding.securityQuestions
        qrViewModel.getSecurityQuestionsResponse.observe(this) {
            val securityQuestionAdapter = SecurityQuestionsAdapter(
                qrViewModel.listOfSecurityQuestions, this,
                android.R.layout.simple_expandable_list_item_1
            )
            getSecurityQuestion.setAdapter(securityQuestionAdapter)
        }

        getSecurityQuestion.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 listOfQuestions =
                    adapterView?.getItemAtPosition(p2) as GetSecurityQuestionResponseItem
                securityQuestionAnswer = listOfQuestions.question
            }
        }

        passwordDialogBinding.proceed.setOnClickListener {
            if (passwordDialogBinding.passwordEdittext.text.toString() == Prefs.getString(
                    PIN_PASSWORD,
                    ""
                )
            ) {
                inputPasswordDialog.dismiss()
            } else {
                Toast.makeText(this, "Incorrect pin", Toast.LENGTH_SHORT).show()
            }
        }

        passwordDialogBinding.update.setOnClickListener {
            getOtpVerificationToUpdatePin()
            getSelectedQuestion()
            updatePinDialog.show()
            inputPasswordDialog.dismiss()
        }
        passwordSetDialog =
            AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                .setView(passwordSetBinding.root)
                .setCancelable(false)
                .create()
        updatePinDialog =
            AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                .setView(updatePinBinding.root)
                .setCancelable(false)
                .create()
        inputPasswordDialog = AlertDialog.Builder(this)
            .setView(passwordDialogBinding.root)
            .setCancelable(false)
            .create()

        if (restorePrefData()) {
            inputPasswordDialog.dismiss()
            inputPasswordDialog.show()
        } else {
            passwordSetDialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

//    private fun navigateToDestination(fragment: Fragment) {
//        val fragmentManager: FragmentManager = supportFragmentManager
//
//        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(
//            R.id.mainActivityfragmentContainerView,
//            fragment
//        )
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//    }

    private fun restorePrefData(): Boolean {
        val pref: SharedPreferences? = application?.getSharedPreferences(
            "MyPref",
            AppCompatActivity.MODE_PRIVATE
        )
        val isIntroActivitySeenBefore: Boolean =
            pref?.getBoolean("IsIntroActivityOpened", false) == true
        return isIntroActivitySeenBefore
    }

    private fun sharedPrefsData() {
        val pref: SharedPreferences? = application?.getSharedPreferences(
            "MyPref",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean("IsIntroActivityOpened", true)
        editor?.apply()
    }

    private fun setTransactionPin(
        transactionPin: String,
        securityQuestionId: String,
        securityQuestion: String,
        securityAnswer: String
    ) {
        qrViewModel.setTransactionPin(transactionPin, securityQuestionId, securityQuestion, securityAnswer)
        observeServerResponseActivity(
            this,
            this,
            qrViewModel.setPINResponse,
            loader,
            supportFragmentManager
        ) {
            qrViewModel.setPINResponse.value?.let {
                if (it.data?.status == "success") {
                  //  Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                }
            }
            Prefs.putString(
                PIN_PASSWORD,
                transactionPin
            )
            sharedPrefsData()
            passwordSetBinding.confirmReprintPasswordEdittext.setText("")
            passwordSetBinding.reprintPasswordEdittext.setText("")
            passwordSetDialog.cancel()
            val header = Singletons().getTallyUserToken()!!
            val token = "Bearer $header"
            qrViewModel.fetchWallet(token)
        }
    }

    private fun getOtpVerificationToUpdatePin() {
        qrViewModel.getOtpVerificationToUpdatePin()
        observeServerResponseActivity(
            this,
            this,
            qrViewModel.getOtpToUpdatePinResponse,
            loader,
            supportFragmentManager
        ) {
            val otpResponse = qrViewModel.getOtpToUpdatePinResponse.value
            otpResponse?.let {
                Toast.makeText(this, otpResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTransactionPin(
        newPin: String,
        otp: String,
        securityAnswer: String,
        securityQuestion: Int
    ) {
        qrViewModel.updateTransactionPin(newPin, otp, securityAnswer, securityQuestion)
        observeServerResponseActivity(
            this,
            this,
            qrViewModel.updatePinResponse,
            loader,
            supportFragmentManager
        ) {
            val otpResponse = qrViewModel.updatePinResponse.value
            otpResponse?.let {
             //   Toast.makeText(this, otpResponse.message, Toast.LENGTH_SHORT).show()
            }
            Prefs.putString(
                PIN_PASSWORD,
                newPin
            )
            sharedPrefsData()
            updatePinBinding.confirmPinEdittext.setText("")
            updatePinBinding.transactionPinEdittext.setText("")
            updatePinDialog.cancel()
        }
    }

    private fun getSelectedQuestion(
    ) {
        qrViewModel.getSelectedQuestion()
        observeServerResponseActivity(
            this,
            this,
            qrViewModel.getSelectedQuestionResponse,
            loader,
            supportFragmentManager
        ) {
            val selectedQuestion = qrViewModel.getSelectedQuestionResponse.value?.data
            selectedQuestion?.let {
                updatePinBinding.securityQuestions.setText(it.question)
                securityQuestion = it.question_id
            }
        }
    }
}