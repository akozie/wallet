package com.woleapp.netpos.qrgenerator.ui.fragments

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.adapter.paging.TransactionPagingAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionBinding
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.TransactionModel
import com.woleapp.netpos.qrgenerator.ui.activities.AuthenticationActivity
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.TransactionViewModel
import io.reactivex.disposables.CompositeDisposable


class TransactionFragment : Fragment(), TransactionAdapter.OnTransactionClick {

    private lateinit var _binding: FragmentTransactionBinding
    private val binding get() = _binding
    private lateinit var userEmail: String
    private var qrCodeID: String? = null
    private val mDisposable = CompositeDisposable()
    private val qrViewModel by activityViewModels<QRViewModel>()
    private val mViewModel by activityViewModels<TransactionViewModel>()
    private lateinit var mAdapter: TransactionPagingAdapter
    private lateinit var loader:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = binding.progressBar
        //loader.visibility = View.VISIBLE

        val userId = Singletons().getCurrentlyLoggedInUser()?.id.toString()
        val num = AppDatabase.getDatabaseInstance(requireContext()).getQrDao()
            .getUserQrCodes(userId)
        if (num.isEmpty()){
            qrCodeID = null
        }else{
            num.forEach {
                qrCodeID = it.qr_code_id
            }
        }

      //  qrCodeID = "67899"
        if (qrCodeID.isNullOrEmpty()){
            loader.visibility = View.GONE
            binding.generateAQr.visibility = View.VISIBLE
           // showToast("Generate a QR code")
        }else{
            transactionSetUp()
        }
        binding.signOut.setOnClickListener{
            AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        // Continue with delete operation
                        Prefs.edit().clear().apply()
                        startActivity(
                            Intent(requireContext(), AuthenticationActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.no, null)
                .show()
        }
//        binding.seeAll.setOnClickListener {
//            val action = TransactionsFragmentDirections.actionTransactionsFragmentToTransactionHistoryFragment()
//            findNavController().navigate(action)
//        }
        userEmail = Singletons().getCurrentlyLoggedInUser()?.email.toString()
        binding.userEmail.text = userEmail

        binding.retry.setOnClickListener {
            transactionSetUp()
        }
    }

    private fun transactionSetUp() {
      //  loader.visibility = View.VISIBLE
        mAdapter = TransactionPagingAdapter()
        mDisposable.add(mViewModel.getTransactions(qrCodeID!!).subscribe {
            mAdapter.submitData(lifecycle, it)
        })
        binding.transactionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.transactionRecycler.adapter = mAdapter

        mAdapter.addLoadStateListener { loadState ->
            binding.apply {
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    mAdapter.itemCount < 1){
                    transactionRecycler.isVisible = false
                    emptyText.isVisible = true
                    retry.isVisible = true
                    seeAll.isVisible = false
                    loader.visibility = View.GONE
                } else {
                    emptyText.isVisible = false
                    retry.isVisible = false
                }
            }
        }

//        qrViewModel.getAllMerchant()
//        observeServerResponse(
//            qrViewModel.allMerchantResponse,
//            loader,
//            requireActivity().supportFragmentManager
//        ) {
//
//        }
    }

//    private fun qrSetUp() {
//        qrViewModel.transactionResponse.value?.data?.data?.rows?.let {
//            qrDetailsDataList = it
//        }
//        if (qrDetailsDataList.isEmpty()){
//            showToast("EMPTY")
//        }else{
//            qrDetailAdapter = QrDetailsAdapter(qrDetailsDataList)
//            qrDetailRecyclerview.adapter = qrDetailAdapter
//            qrDetailRecyclerview.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//
//        }
//
//    }

    override fun onTransactionClicked(transaction: TransactionModel) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDetailsFragment()
        findNavController().navigate(action)
    }
}

