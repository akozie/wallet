package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentRecentTransactionsBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentSignInOrGenerateQRBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel
import java.util.ArrayList


class RecentTransactionsFragment : Fragment(), TransactionAdapter.OnTransactionClick  {

    private var _binding: FragmentRecentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionDataList: ArrayList<TransactionModel>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateTransactionData()
        transactionSetUp()
    }
    private fun transactionSetUp() {
        transactionAdapter = TransactionAdapter(transactionDataList, this)
        binding.recentTransactionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recentTransactionRecycler.adapter = transactionAdapter
    }

    private fun generateTransactionData(){
        transactionDataList = arrayListOf()
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
    }

    override fun onTransactionClicked(transaction: TransactionModel) {
        //
    }
}