package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentAllTransactionsBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentDisplayQrBinding
import com.woleapp.netpos.qrgenerator.ui.model.GenerateQRResponse

class DisplayQrFragment : Fragment() {

    private lateinit var _binding: FragmentDisplayQrBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDisplayQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrCode =  arguments?.getParcelable<GenerateQRResponse>("QR")?.data
        if (qrCode.isNullOrEmpty()){
            Glide.with(requireContext()).load(R.drawable.providus_verve).into(binding.qrCode)
        }else{
            Glide.with(requireContext()).load(qrCode).into(binding.qrCode)
        }
    }
}