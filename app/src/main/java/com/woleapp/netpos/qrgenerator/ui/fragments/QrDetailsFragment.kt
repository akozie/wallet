package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.QrDetailsAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentQrDetailsBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.model.Transaction
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponseItem
import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponseOnce
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel


class QrDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentQrDetailsBinding
    private val binding get() = _binding
    private lateinit var qrViewPager2: ImageView
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var loader: AlertDialog
    private lateinit var qrDetailAdapter: QrDetailsAdapter
    private lateinit var qrDetailRecyclerview: RecyclerView
    private lateinit var qrDetailsDataList: List<Transaction>
    private lateinit var qrCode: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrDetailsBinding.inflate(inflater, container, false)
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        qrCode = arguments?.getParcelable<GenerateQRResponse>("DETAILSQR")?.qr_code_id.toString()
        qrViewModel.getEachTransaction(qrCode)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrViewPager2 = binding.qrPager
        qrDetailRecyclerview = binding.recyclerview

        loader = alertDialog(requireContext())
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        getQrTransactions()
    }

    private fun getQrTransactions() {
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        observeServerResponse(
            qrViewModel.transactionResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            qrViewModel.transactionResponse.value?.data?.data?.rows?.let {
                qrDetailsDataList = it
            }
            qrSetUp()
        }

    }

    private fun qrSetUp() {
        if (qrDetailsDataList.isEmpty()) {
            binding.noTransaction.visibility = View.VISIBLE
            qrDetailRecyclerview.visibility = View.GONE
        } else {
            binding.noTransaction.visibility = View.GONE
            qrDetailRecyclerview.visibility = View.VISIBLE
            qrDetailAdapter = QrDetailsAdapter(qrDetailsDataList)
            qrDetailRecyclerview.adapter = qrDetailAdapter
            qrDetailRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        qrViewModel.transactionResponse.removeObservers(viewLifecycleOwner)
        qrViewModel.clear()
        super.onDestroyView()
    }

}