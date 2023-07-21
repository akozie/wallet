package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentSignInOrGenerateQRBinding
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.isDarkModeEnabled
import com.woleapp.netpos.qrgenerator.utils.showToast


class SignInOrGenerateQRFragment : Fragment() {

    private var _binding: FragmentSignInOrGenerateQRBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignInOrGenerateQRBinding.inflate(inflater, container, false)
        val isDarkMode = isDarkModeEnabled(resources.configuration)
        if (isDarkMode) {
            // App is in dark mode
            binding.imageView.setImageResource(R.drawable.tally_logo_white)
        } else {
            // App is not in dark mode
            binding.imageView.setImageResource(R.drawable.tally_logo)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            val action =
                SignInOrGenerateQRFragmentDirections.actionSignInOrGenerateQRFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
        binding.signInButton.setOnClickListener {
            val action =
                SignInOrGenerateQRFragmentDirections.actionSignInOrGenerateQRFragmentToSignInFragment()
            findNavController().navigate(action)
        }
        binding.generateQrButton.setOnClickListener {
            val action =
                SignInOrGenerateQRFragmentDirections.actionSignInOrGenerateQRFragmentToGenerateQrFragment()
            findNavController().navigate(action)
        }
        binding.needACard.setOnClickListener {
            val action =
                SignInOrGenerateQRFragmentDirections.actionSignInOrGenerateQRFragmentToNeedCardWebViewFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        val isDarkMode = isDarkModeEnabled(resources.configuration)
        if (isDarkMode) {
            // App is in dark mode
            binding.imageView.setImageResource(R.drawable.tally_logo_white)
        } else {
            // App is not in dark mode
            binding.imageView.setImageResource(R.drawable.tally_logo)
        }
        super.onResume()

    }

}