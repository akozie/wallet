package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentSignInBinding
import com.woleapp.netpos.qrgenerator.ui.activities.MainActivity
import com.woleapp.netpos.qrgenerator.ui.model.LoginRequest
import com.woleapp.netpos.qrgenerator.ui.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.ui.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.ui.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.ui.utils.showToast
import com.woleapp.netpos.qrgenerator.ui.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var _binding: FragmentSignInBinding
    private val binding get() = _binding
    private lateinit var emailAddress: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        loader = RandomUtils.alertDialog(requireContext(), R.layout.layout_loading_dialog)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            passwordView = signInPassword
            emailAddress = signInEmail
        }
    }

    private fun generateQr() {
        when {
            emailAddress.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_email))
            }
            passwordView.text.toString().isEmpty() -> {
                showToast(getString(R.string.all_please_enter_password))
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    signIn()
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        emailAddress.doOnTextChanged { _, _, _, _ ->
            when {
                emailAddress.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_email))
                    isValidated = false
                }
                else -> {
                    binding.fragmentEnterEmail.error = null
                    isValidated = true
                }
            }
        }
        passwordView.doOnTextChanged { _, _, _, _ ->
            when {
                passwordView.text.toString().trim().isEmpty() -> {
                    showToast(getString(R.string.all_please_enter_password))
                    isValidated = false
                }
                else -> {
                    binding.fragmentEnterPassword.error = null
                    isValidated = true
                }
            }
        }
        return isValidated
    }

    private fun signIn() {
        val loginUser = LoginRequest(
            password = passwordView.text.toString().trim(),
            email = emailAddress.text.toString().trim()
        )
        qrViewModel.login(
            loginUser
        )
        observeServerResponse(qrViewModel.generateQrResponse, loader, requireActivity().supportFragmentManager){
            startActivity(
                Intent(requireContext(), MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

    }

}