package com.woleapp.netpos.qrgenerator.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.LayoutQrReceiptPdfBinding
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import java.text.DecimalFormat


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

//convert image to bitmap
fun getBitMap(view: View): Bitmap {
    val bitMap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    val bgDrawable = view.background
    if (bgDrawable!= null){
        bgDrawable.draw(canvas)
    }else{
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return bitMap
}

fun initViewsForPdfLayout(
    pdfView: ViewDataBinding,
    receipt: Any?
) {
    when (receipt) {
        is QrTransactionResponseModel -> {
            initViewsForQrReceipt((pdfView as LayoutQrReceiptPdfBinding), receipt)
        }
//        is TransactionResponse -> {
//            initViewsForPosReceipt((pdfView as LayoutPosReceiptPdfBinding), receipt)
//        }
        else -> { /* Do nothing */
        }
    }
}

private fun initViewsForQrReceipt(
    pdfView: LayoutQrReceiptPdfBinding,
    responseFromWebView: QrTransactionResponseModel?
) {
    pdfView.apply {
        responseFromWebView?.let { respFromWebView: QrTransactionResponseModel ->
//            merchantName.text = pdfView.root.context.getString(
//                R.string.merchant_name_place_holder,
//                Singletons.getCurrentlyLoggedInUser()?.business_name
//                    ?: "${BuildConfig.FLAVOR} POS MERCHANT"
//            )
            cardOwner.text = pdfView.root.context.getString(
                R.string.card_owner_place_holder,
                respFromWebView.customerName
            )
//            dateTime.text =
//                pdfView.appVersion.context.getString(
//                    R.string.date_time_place_holder,
//                    respFromWebView.
//                )
            transAmount.text = pdfView.appVersion.context.getString(
                R.string.amount_place_holder,
                respFromWebView.amount.toDouble().formatCurrency()
            )
            orderId.text = pdfView.appVersion.context.getString(
                R.string.order_id_place_holder,
                respFromWebView.orderId
            )
            narration.text =
                pdfView.appVersion.context.getString(
                    R.string.narration_place_holder,
                    respFromWebView.narration
                )
//            transId.text =
//                pdfView.appVersion.context.getString(
//                    R.string.trans_ref_place_holder,
//                    respFromWebView.transId
//                )
            status.text =
                pdfView.appVersion.context.getString(
                    R.string.transaction_status_place_holder,
                    respFromWebView.status.uppercase()
                )
            responseCode.text =
                pdfView.appVersion.context.getString(
                    R.string.response_code_place_holder,
                    respFromWebView.code
                )
            message.text = pdfView.appVersion.context.getString(
                R.string.message_place_holder,
                respFromWebView.message
            )
//            appVersion.text = pdfView.appVersion.context.getString(
//                R.string.app_version_place_holder,
//                "${BuildConfig.FLAVOR} POS ${BuildConfig.VERSION_NAME}"
//            )
        }
    }
}

