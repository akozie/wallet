package com.woleapp.netpos.qrgenerator.model.verve

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.utils.SAVE_CUSTOMER_DETAILS
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerveOTPResponse(
    val amount: String,
    val code: String,
    val message: String,
    val orderId: String,
    val result: String,
    val status: String,
    val transId: String
) : Parcelable {

    fun mapTOQrTransactionModel() : QrTransactionResponseModel {
        val customerName = Prefs.getString(SAVE_CUSTOMER_DETAILS, "")
        return if (customerName.isNotEmpty()){
            QrTransactionResponseModel(
                amount = amount,
                code = code,
                currency_code = "NGN",
                customerName = customerName.split("===").first(),
                email = customerName.split("===").last(),
                message = message,
                narration = "Qr payment using Verve Card",
                orderId = orderId,
                result = result,
                status = status,
                transId = transId
            )
        }else {
            QrTransactionResponseModel(
                amount = amount,
                code = code,
                currency_code = "NGN",
                customerName = "Customer",
                email = "Customer email",
                message = message,
                narration = "Qr payment using Verve Card",
                orderId = orderId,
                result = result,
                status = status,
                transId = transId
            )
        }
    }
}

