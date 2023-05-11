package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.woleapp.netpos.qrgenerator.databinding.FragmentSignInOrGenerateQRBinding


class SignInOrGenerateQRFragment : Fragment() {

    private var _binding: FragmentSignInOrGenerateQRBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInOrGenerateQRBinding.inflate(inflater, container, false)
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
}