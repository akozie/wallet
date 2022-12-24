package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentMerchantsBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionsBinding


class MerchantsFragment : Fragment() {

    private lateinit var _binding: FragmentMerchantsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMerchantsBinding.inflate(inflater, container, false)
        return binding.root
    }

}