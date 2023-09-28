package com.woleapp.netpos.qrgenerator.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentRecentTransactionDetailsBinding
import com.woleapp.netpos.qrgenerator.model.wallet.TallyWalletUserTransactionsResponseItem
import com.woleapp.netpos.qrgenerator.model.wallet.request.TransactionReceiptRequest
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.getBitMap
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named


class RecentTransactionDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecentTransactionDetailsBinding
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: android.app.AlertDialog
    private lateinit var receipt: String
    private lateinit var newReceipt: TransactionReceiptRequest

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    @Named("io-scheduler")
    lateinit var ioScheduler: Scheduler

    @Inject
    @Named("main-scheduler")
    lateinit var mainThreadScheduler: Scheduler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_recent_transaction_details,
            container,
            false
        )
        loader = alertDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userTransactions =
            arguments?.getParcelable<TallyWalletUserTransactionsResponseItem>("USERTRANSACTION")

        walletViewModel.fetchWalletMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        binding.transAmount.text = getString(
            R.string.amount_place_holder,
            userTransactions?.transaction_amount?.toDouble()?.formatCurrency()
        )
        binding.transId.text =
            getString(R.string.trans_ref_place_holder, userTransactions?.transaction_id)
        binding.cardOwner.text =
            getString(R.string.beneficiary_place_holder, userTransactions?.destination_acct_name)
        binding.narration.text =
            getString(R.string.beneficiary_account_place_holder, userTransactions?.destination_acct)
        binding.sourceAccount.text =
            getString(R.string.source_account_place_holder, userTransactions?.source_acct)
        val date = userTransactions?.created_at?.substring(0, 10)
        val time = userTransactions?.created_at?.substring(11, 16)
        if (time?.substring(0, 2)!! < 12.toString()) {
            binding.dateTime.text = getString(R.string.date_time_place_holder, "$date $time AM")
        } else {
            val newTime = time.substring(0, 2).toInt()
            val calculatedTime = newTime - 12
            val remainingTime = time.substring(3, 5)
            if (calculatedTime == 0) {
                binding.dateTime.text =
                    getString(R.string.date_time_place_holder, "$date 12:$remainingTime PM")
            } else if (calculatedTime < 10) {
                binding.dateTime.text = getString(
                    R.string.date_time_place_holder,
                    "$date 0$calculatedTime:$remainingTime PM"
                )
            } else {
                binding.dateTime.text = getString(
                    R.string.date_time_place_holder,
                    "$date $calculatedTime:$remainingTime PM"
                )
            }
        }

        binding.shareReceipt.setOnClickListener {
            share()
        }


        val transId = userTransactions.transaction_id
        newReceipt = TransactionReceiptRequest(
            transaction_id = transId
        )
        if (!transId.isNullOrEmpty()) {
            //  getReceipt(newReceipt)
        }

        binding.sendEmail.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        walletViewModel.sendEmailReceipt(requireContext(),
            newReceipt
        )
        observeServerResponse(
            walletViewModel.sendEmailReceiptResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
//            walletViewModel.sendEmailReceiptResponse.value?.let {
//                showToast("Receipt sent to your email")
//            }
        }
    }

    private fun share() {
        val b: Bitmap = getBitMap(binding.receipt)

        try {
            val f = File(requireContext().externalCacheDir, "forShare.jpg")
            val outputStream = FileOutputStream(f)
            b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            f.setReadable(true, false)


            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            val photo: Uri = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                f
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, photo)
            shareIntent.type = "image/*"
            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onDestroyView() {
        walletViewModel.clear()
        super.onDestroyView()
    }
}