package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.RegisterRequest
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.pay.PayModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QRRepository @Inject constructor(
    private val qrService: QRService,
    private val transactionService: TransactionService,
    private val checkoutService: CheckoutService,
    private val merchantService: MerchantService,
    private val walletService: WalletService
) {

    fun login(loginRequest: LoginRequest) = qrService.login(loginRequest)

    fun register(registerRequest: RegisterRequest) = qrService.register(registerRequest)

    fun generateQR(qrModelRequest: QrModelRequest) = qrService.generateQR(qrModelRequest)

    fun getCardSchemes() = qrService.getCardSchemes()

    fun getCardBanks() = qrService.getCardBanks()


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


}