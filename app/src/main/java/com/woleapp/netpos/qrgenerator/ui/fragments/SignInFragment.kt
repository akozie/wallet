package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentSignInBinding
import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.User
import com.woleapp.netpos.qrgenerator.ui.activities.MainActivity
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var _binding: FragmentSignInBinding
    private val binding get() = _binding
    private lateinit var emailAddress: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private lateinit var loginButton: Button
    private val qrViewModel by viewModels<QRViewModel>()
    private lateinit var loader: ProgressBar
    private lateinit var user: User

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
        qrViewModel.loginMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
                loginButton.isEnabled = true
            }
        }
        loader = binding.signInProgressBar
        initViews()
        loginButton.setOnClickListener {
            login()
        }

    }

    private fun initViews() {
        with(binding) {
            passwordView = signInPassword
            emailAddress = signInEmail
            loginButton = signInButton
        }
    }

    private fun login() {
        when {
            emailAddress.text.toString().isEmpty() -> {
                binding.fragmentEnterEmail.error =
                    getString(R.string.all_please_enter_email)
                binding.fragmentEnterEmail.errorIconDrawable = null
            }
            passwordView.text.toString().isEmpty() -> {
                binding.fragmentEnterPassword.error =
                    getString(R.string.all_please_enter_password)
                binding.fragmentEnterPassword.errorIconDrawable = null
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
                    binding.fragmentEnterEmail.error =
                        getString(R.string.all_please_enter_email)
                    isValidated = false
                }
                emailAddress.text.toString().trim().isNotEmpty() -> {
                    binding.fragmentEnterEmail.error = ""
                    isValidated = true
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
                    binding.fragmentEnterPassword.error =
                        getString(R.string.all_please_enter_password)
                    isValidated = false
                }
                passwordView.text.toString().trim().isEmpty() -> {
                    binding.fragmentEnterPassword.error = ""
                    isValidated = true
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
        observeServerResponse(
            qrViewModel.loginResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            loader.visibility = View.GONE
            startActivity(
                Intent(requireContext(), MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            requireActivity().finish()
        }
    }
}