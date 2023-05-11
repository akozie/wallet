package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyQrBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentSendWithTallyQrResultBinding


class SendWithTallyQrResultFragment : Fragment() {

    private lateinit var binding: FragmentSendWithTallyQrResultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_send_with_tally_qr_result, container, false)
        return binding.root
    }
}