package com.woleapp.netpos.qrgenerator.network

import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.RegisterRequest
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.pay.PayModel
import com.woleapp.netpos.qrgenerator.model.verve.SendOtpForVerveCardModel
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QRRepository @Inject constructor(
    private val qrService: QRService,
    private val transactionService: TransactionService,
    private val checkoutService: CheckoutService
) {

    fun login(loginRequest: LoginRequest) = qrService.login(loginRequest)

    fun logout(logoutRequest: JsonObject?) = qrService.logout(logoutRequest)

    fun register(registerRequest: RegisterRequest) = qrService.register(registerRequest)

   // fun getNewToken(newTokenRequest: NewTokenRequest) = qrService.getNewToken(newTokenRequest)

    fun generateQR(qrModelRequest: QrModelRequest) = qrService.generateQR(qrModelRequest)

    fun getCardSchemes() = qrService.getCardSchemes()

    fun getCardBanks() = qrService.getCardBanks()

    fun fetchQrToken() =
        qrService.fetchUserQrs()


    fun getAllTransaction(qrCodeId: String) = transactionService.getEachTransaction(qrCodeId, 1, 10)


    fun checkOut(checkOutModel: CheckOutModel) = checkoutService.checkOut(
        checkOutModel.merchantId,
        checkOutModel.name,
        checkOutModel.email,
        checkOutModel.amount,
        checkOutModel.currency,
        checkOutModel.orderId
    )

    fun pay(clientData: String) = checkoutService.pay(PayModel(clientData,"PAY"))


    fun consummateTransactionBySendingOtp(otpPayLoad: SendOtpForVerveCardModel): Single<Response<JsonObject>> =
        checkoutService.sendOtpForVerveCard(otpPayLoad)
}