package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRFragment : Fragment(), QrAdapter.OnQrClick {
    private lateinit var _binding: FragmentQRBinding
    private val binding get() = _binding
    private lateinit var qrAdapter: QrAdapter
    private lateinit var qrDataList: ArrayList<QrModel>
    private val qrViewModel by activityViewModels<QRViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQRBinding.inflate(inflater, container, false)
        if (BuildConfig.FLAVOR.contains("qrgenerator")) {
            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        activity?.finishAffinity()
                    }
                })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getQrCodes()
        qrSetUp()
        binding.button.setOnClickListener {
            val action =
                TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }
    }


    private fun qrSetUp() {
        qrAdapter = QrAdapter(qrDataList, this)
        binding.qrRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.qrRecycler.adapter = qrAdapter
    }

    private fun getQrCodes() {
        qrDataList = arrayListOf()
        val userId = Singletons().getCurrentlyLoggedInUser()?.id.toString()
        val num = AppDatabase.getDatabaseInstance(requireContext()).getQrDao()
            .getUserQrCodes(userId)
        num.forEach {
            qrDataList.add(
                QrModel(
                    it.data!!,
                    it.qr_code_id!!,
                    it.card_scheme!!,
                    it.date!!,
                    it.issuing_bank!!
                )
            )
        }
    }

    override fun viewTransaction(qrModel: QrModel) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToQrDetailsFragment2(qrModel)
        findNavController().navigate(action)
    }

    override fun onViewQr(qrModel: QrModel) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToDisplayQrFragment23(qrModel)
        findNavController().navigate(action)
    }
}
