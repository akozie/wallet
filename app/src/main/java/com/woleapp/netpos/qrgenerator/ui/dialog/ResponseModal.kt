package com.woleapp.netpos.qrgenerator.ui.dialog

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.LayoutQrReceiptPdfBinding
import com.woleapp.netpos.qrgenerator.databinding.TransactionStatusModalBinding
import com.woleapp.netpos.qrgenerator.model.pay.ModalData
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.ui.fragments.GenerateQrFragmentDirections
import com.woleapp.netpos.qrgenerator.utils.QR_TRANSACTION_RESULT_BUNDLE_KEY
import com.woleapp.netpos.qrgenerator.utils.QR_TRANSACTION_RESULT_REQUEST_KEY
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.initViewsForPdfLayout
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class ResponseModal @Inject constructor() : DialogFragment() {
    private lateinit var binding: TransactionStatusModalBinding
    private lateinit var lottieIcon: LottieAnimationView
    private lateinit var statusTv: TextView
    private lateinit var cancelBtn: ImageView
    private lateinit var sendReceiptAsSms: ImageView
    private lateinit var amountTv: TextView
    private var modalData: ModalData? = null
     private lateinit var pdfView: LayoutQrReceiptPdfBinding
    private var responseFromWebView: QrTransactionResponseModel? = null
   // private val scanQrViewModel by activityViewModels<ScanQrViewModel>()
    private val transactionViewModel by activityViewModels<TransactionViewModel>()
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var outputStream : OutputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(QR_TRANSACTION_RESULT_REQUEST_KEY) { _, bundle ->
            responseFromWebView =
                bundle.getParcelable(QR_TRANSACTION_RESULT_BUNDLE_KEY)
//            modalData =
                responseFromWebView?.let { it1 ->
                    Log.d("RESULT", it1.toString())
//                    if (it1 is QrTransactionResponseModel && it1 != null) {
//                 //       Log.d("AMOUNT", it1.amount)
//                        if (it1.code == "90"){
//                            val getQrModel = Singletons().getSavedQrModelRequest()
//                            getQrModel?.let {
//                                qrViewModel.generateQR(it, requireContext())
//                            }
//                        }else{
//                            //
//                        }
//                        // saveTransactionIntoDb(responseFromWebView!!)
//                        transactionViewModel.saveQrTransaction(it1)
//                        ModalData(
//                            it1.code == "00",
//                            it1.amount.toDouble()
//                        )
//                    } else ModalData(false, 0.0)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        pdfView =
            DataBindingUtil.inflate(inflater, R.layout.layout_qr_receipt_pdf, container, false)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.transaction_status_modal, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.curve_bg)
            isCancelable = false
        }
        initViewsForPdfLayout(
            pdfView,
            responseFromWebView
        )
    }

    override fun onResume() {
        super.onResume()
        setData()
        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
        sendReceiptAsSms.setOnClickListener {
            dialog?.dismiss()
            responseFromWebView?.let { qrTransResponse ->
                transactionViewModel.saveQrTransaction(qrTransResponse)
//                nfcCardReaderViewModel.showReceiptDialogForQrPayment()
            }
            observeServerResponse(
                qrViewModel.generateQrResponse,
                //loader,
                requireActivity().supportFragmentManager
            ) {
                val action =
                    GenerateQrFragmentDirections.actionGenerateQrFragmentToShowQrFragment(qrViewModel.generateQrResponse.value?.data!!)
                findNavController().navigate(action)
            }
        }
    }

    private fun initViews() {
        with(binding) {
            lottieIcon = statusIconLAV
            statusTv = successFailed
            cancelBtn = cancelButton
            amountTv = qrAmount
            sendReceiptAsSms = printReceipt
        }
    }

    private fun setData() {
        modalData?.let {
            if (it.status) {
                lottieIcon.setAnimation(R.raw.lottiesuccess)
                statusTv.text = getString(R.string.success)
                statusTv.setTextColor(resources.getColor(R.color.success))
            } else {
                lottieIcon.setAnimation(R.raw.failed)
                statusTv.text = getString(R.string.failed)
                statusTv.setTextColor(resources.getColor(R.color.failed))
            }
            amountTv.text = it.amount.toLong().formatCurrency("\u20A6")
        }
    }

//    private fun saveImage(bitmapImage: BitmapDrawable) {
//
////        val drawable = binding.qrCode.getDrawable() as BitmapDrawable
//        val bitmap = bitmapImage.bitmap
//        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
//        // val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "")
//        if (!dir.exists()) {
//            dir.mkdir()
//        }
//        val file = File(dir, System.currentTimeMillis().toString() + ".jpg")
//
//        outputStream = FileOutputStream(file)
//        try {
//            outputStream = FileOutputStream(file)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        Toast.makeText(requireContext(), "Successfuly Saved", Toast.LENGTH_SHORT).show()
//        try {
//            outputStream.flush()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        try {
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

}
