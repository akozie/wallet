package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.databinding.FragmentDisplayQrBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.ui.activities.MainActivity
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel


class DisplayQrFragment : Fragment() {

    private lateinit var _binding: FragmentDisplayQrBinding
    private val binding get() = _binding
    private lateinit var viewQr: GenerateQRResponse
    private val qrViewModel by activityViewModels<QRViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDisplayQrBinding.inflate(inflater, container, false)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                    // if you want onBackPressed() to be called as normal afterwards
                }
            }
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrCode = arguments?.getParcelable<QrModel>("DISPLAYQR")?.data
        if (qrCode.isNullOrEmpty()) {
            qrViewModel.generateQrResponse.value?.data?.data?.let {
                Glide.with(requireContext()).load(it).into(binding.qrCode)
            }
        } else {
            Glide.with(requireContext()).load(qrCode).into(binding.qrCode)
        }
    }


}