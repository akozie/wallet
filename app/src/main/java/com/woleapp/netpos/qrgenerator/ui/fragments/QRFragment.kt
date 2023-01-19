package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterPasswordPrefBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutSetPasswordPrefBinding
import com.woleapp.netpos.qrgenerator.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.utils.Singletons
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class QRFragment : Fragment(), QrAdapter.OnQrClick {

    private lateinit var _binding: FragmentQRBinding
    private val binding get() = _binding
    private lateinit var qrAdapter: QrAdapter
    private lateinit var qrDataList: ArrayList<QrModel>
    private lateinit var inputPasswordDialog: android.app.AlertDialog
    private lateinit var passwordEnterBinding: LayoutEnterPasswordPrefBinding
    private lateinit var passwordSetDialog: android.app.AlertDialog
    private lateinit var passwordSetBinding: LayoutSetPasswordPrefBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentQRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //generateQrData()
        //getList()

        getQrCodes()
        qrSetUp()

        binding.button.setOnClickListener {
            val action = TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }
    }

    private fun qrSetUp() {
        qrAdapter = QrAdapter(qrDataList, this)
        binding.qrRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.qrRecycler.adapter = qrAdapter
    }

    private fun getQrCodes() {
        qrDataList = arrayListOf()
        val userId = Singletons().getCurrentlyLoggedInUser()?.id.toString()
        val num = AppDatabase.getDatabaseInstance(requireContext()).qrDao()
            .getUserQrCodes(userId)
        num.forEach {
            qrDataList.add(QrModel(it.data!!, it.qr_code_id!!, it.card_scheme!!, it.date!!, it.issuing_bank!!))
        }
    }

    override fun viewTransaction(qrModel: QrModel) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToQrDetailsFragment2(
                qrModel
            )
            findNavController().navigate(action)
    }

    override fun onViewQr(qrModel: QrModel) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToDisplayQrFragment23(qrModel)
        findNavController().navigate(action)
    }
}