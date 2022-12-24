package com.woleapp.netpos.qrgenerator.ui.fragments


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentGenerateQrBinding
import com.woleapp.netpos.qrgenerator.ui.activities.MainActivity
import com.woleapp.netpos.qrgenerator.ui.adapter.BankCardAdapter
import com.woleapp.netpos.qrgenerator.ui.adapter.CardSchemeAdapter
import com.woleapp.netpos.qrgenerator.ui.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.ui.model.RegisterRequest
import com.woleapp.netpos.qrgenerator.ui.model.Row
import com.woleapp.netpos.qrgenerator.ui.model.RowX
import com.woleapp.netpos.qrgenerator.ui.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.ui.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.ui.utils.showToast
import com.woleapp.netpos.qrgenerator.ui.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class GenerateQrFragment : Fragment() {

    private lateinit var _binding: FragmentGenerateQrBinding
    private val binding get() = _binding
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var full_name: TextInputEditText
    private lateinit var emailAddress: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var qrIssuingBank: AutoCompleteTextView
    private lateinit var qrCardScheme: AutoCompleteTextView
    private lateinit var cardExpiryNumber: TextInputEditText
    private lateinit var cardExpiryDate: TextInputEditText
    private lateinit var cardExpiryCvv: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private lateinit var loader: AlertDialog
    private lateinit var submitBtn: Button
    private lateinit var bankCard:String
    private lateinit var cardScheme:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         _binding = FragmentGenerateQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrViewModel.generateQrMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
        qrViewModel.registerMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
        loader = alertDialog(requireContext(), R.layout.layout_loading_dialog)
        initViews()
//        qrViewModel.getCardSchemes()
        qrViewModel.cardSchemeResponse.observe(viewLifecycleOwner){
            val cardSchemeAdapter = CardSchemeAdapter(
                qrViewModel.cardSchemes, requireContext(),
                android.R.layout.simple_expandable_list_item_1
            )
            qrCardScheme.setAdapter(cardSchemeAdapter)
        }
//        qrViewModel.getCardBanks()
        qrViewModel.issuingBankResponse.observe(viewLifecycleOwner){
            val bankCardAdapter = BankCardAdapter(
                qrViewModel.issuingBank, requireContext(),
                android.R.layout.simple_expandable_list_item_1
            )
            qrIssuingBank.setAdapter(bankCardAdapter)
        }

        submitBtn.setOnClickListener {
            if (passwordView.text.toString().trim().isNullOrEmpty()){
                generateQr()
            }else{
                register()
            }
        }

        cardExpiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 2 && !s.toString().contains("/")) {
                    s!!.append("/")
                }
            }
        })

        qrCardScheme.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category  = adapterView?.getItemAtPosition(p2) as Row
                cardScheme = category.card_scheme
            }
        }
        qrIssuingBank.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category  = adapterView?.getItemAtPosition(p2) as RowX
                bankCard = category.bank_id
            }
        }
    }

    private fun initViews() {
        with(binding) {
            full_name = fullName
            emailAddress = email
            phoneNumber = mobileNumber
            qrIssuingBank = issuingBank
            qrCardScheme = cardScheme
            cardExpiryNumber = cardNumber
            cardExpiryDate = expiryDate
            cardExpiryCvv = cardCvv
            passwordView = generatePassword
            submitBtn = btnGenerateQr
        }
    }

    private fun generateQr() {
        when {
            full_name.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_full_name))
            }
            emailAddress.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_email))
            }
            phoneNumber.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_phone_number))
            }
            qrIssuingBank.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_issuing_bank))
            }
            qrIssuingBank.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_issuing_bank))
            }
            qrCardScheme.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_issuing_card_scheme))
            }
            cardExpiryNumber.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_card_expiry_number))
            }
            cardExpiryDate.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_card_date))
            }
            cardExpiryCvv.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_card_cvv))
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    generateEachQr()
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        full_name.doOnTextChanged { _, _, _, _ ->
            when {
                full_name.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_full_name))
                    isValidated = false
                }
                else -> {
                    binding.fragmentFullName.error = null
                    isValidated = true
                }
            }
        }
        emailAddress.doOnTextChanged { _, _, _, _ ->
            when {
                emailAddress.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_email))
                    isValidated = false
                }
                else -> {
                    binding.fragmentEmail.error = null
                    isValidated = true
                }
            }
        }
        phoneNumber.doOnTextChanged { _, _, _, _ ->
            when {
                phoneNumber.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_phone_number))
                    isValidated = false
                }
                else -> {
                    binding.fragmentMobileNumber.error = null
                    isValidated = true
                }
            }
        }
        qrIssuingBank.doOnTextChanged { _, _, _, _ ->
            when {
                qrIssuingBank.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_issuing_bank))
                    isValidated = false
                }
                else -> {
                    binding.fragmentSelectBank.error = null
                    isValidated = true
                }
            }
        }
        qrCardScheme.doOnTextChanged { _, _, _, _ ->
            when {
                qrCardScheme.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_issuing_card_scheme))
                    isValidated = false
                }
                else -> {
                    binding.fragmentSelectCardScheme.error = null
                    isValidated = true
                }
            }
        }
        cardExpiryNumber.doOnTextChanged { _, _, _, _ ->
            when {
                cardExpiryNumber.text.toString().trim().isEmpty() -> {
                        showToast(getString(R.string.all_please_enter_card_expiry_number))
                    isValidated = false
                }
                else -> {
                    binding.fragmentCardNumber.error = null
                    isValidated = true
                }
            }
        }
        cardExpiryDate.doOnTextChanged { _, _, _, _ ->
            when {
                cardExpiryDate.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_card_date))
                    isValidated = false
                }
                else -> {
                    binding.fragmentCardExpiryDate.error = null
                    isValidated = true
                }
            }
        }
        cardExpiryCvv.doOnTextChanged { _, _, _, _ ->
            when {
                cardExpiryCvv.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_card_cvv))
                    isValidated = false
                }
                else -> {
                    binding.fragmentCardCvv.error = null
                    isValidated = true
                }
            }
        }
//        passwordView.doOnTextChanged { _, _, _, _ ->
//            when {
//                passwordView.text.toString().trim().isEmpty() -> {
//                    binding.confirmPasswordField.error =
//                        getString(R.string.all_please_enter_confirm_password)
//                    binding.confirmPasswordField.errorIconDrawable =
//                        null
//                    isValidated = false
//                }
//                else -> {
//                    binding.confirmPasswordField.error = null
//                    isValidated = true
//                }
//            }
//        }
        return isValidated
    }

    private fun generateEachQr() {
        val generateQr = QrModelRequest(
            fullname = full_name.text.toString().trim(),
            email = emailAddress.text.toString().trim(),
            card_cvv = cardExpiryCvv.text.toString().trim(),
            card_expiry = cardExpiryDate.text.toString().trim(),
            card_number = cardExpiryNumber.text.toString().trim(),
            card_scheme = qrCardScheme.text.toString().trim(),
            issuing_bank = bankCard,
            mobile_phone = phoneNumber.text.toString().trim()
        )
        qrViewModel.generateQR(
            generateQr,
            requireContext()
        )
        observeServerResponse(qrViewModel.generateQrResponse, loader, requireActivity().supportFragmentManager){
            val action = GenerateQrFragmentDirections.actionGenerateQrFragmentToDisplayQrFragment(qrViewModel.generateQrResponse.value?.data!!)
            findNavController().navigate(action)
        }

    }

    private fun register() {
        val registerRequest = RegisterRequest(
            fullname = full_name.text.toString().trim(),
            email = emailAddress.text.toString().trim(),
            mobile_phone = phoneNumber.text.toString().trim(),
            password = passwordView.text.toString().trim()
        )
        qrViewModel.register(
            registerRequest
        )
        val generateQr = QrModelRequest(
            fullname = full_name.text.toString().trim(),
            email = emailAddress.text.toString().trim(),
            card_cvv = cardExpiryCvv.text.toString().trim(),
            card_expiry = cardExpiryDate.text.toString().trim(),
            card_number = cardExpiryNumber.text.toString().trim(),
            card_scheme = qrCardScheme.text.toString().trim(),
            issuing_bank = bankCard,
            mobile_phone = phoneNumber.text.toString().trim()
        )
        qrViewModel.generateQR(
            generateQr,
            requireContext()
        )
        observeServerResponse(qrViewModel.registerResponse, loader, requireActivity().supportFragmentManager){
            startActivity(
                Intent(requireContext(), MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
        observeServerResponse(qrViewModel.generateQrResponse, loader, requireActivity().supportFragmentManager){}

    }
}