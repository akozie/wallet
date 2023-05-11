package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woleapp.netpos.qrgenerator.databinding.FragmentMerchantDetailsBinding
import com.woleapp.netpos.qrgenerator.model.Merchant


class MerchantDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentMerchantDetailsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMerchantDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val merchantDetails = arguments?.getParcelable<Merchant>("MERCHANT")

        if (merchantDetails != null) {
            binding.merchantDetailsName.text = merchantDetails.contact_name
            binding.merchantDetailsAddress.text = merchantDetails.address
            binding.merchantDetailsPhoneNumber.text = merchantDetails.mobile_phone
            binding.merchantDetailsEmail.text = merchantDetails.email
        }

    }

}