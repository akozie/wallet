package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransferBinding

class TransferFragment : Fragment() {

    private lateinit var binding : FragmentTransferBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transfer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendWithTallyQrButton.setOnClickListener {
            val action = TransferFragmentDirections.actionTransferFragmentToSendWithTallyQrFragment()
            findNavController().navigate(action)
        }
        binding.sendWithTallyNumberButton.setOnClickListener {
            val action = TransferFragmentDirections.actionTransferFragmentToSendWithTallyNumberFragment()
            findNavController().navigate(action)
        }
        binding.requestPayment.setOnClickListener {
            //
        }
        binding.providusBank.setOnClickListener {
            //
        }
        binding.otherBanks.setOnClickListener {
            //
        }
    }
}