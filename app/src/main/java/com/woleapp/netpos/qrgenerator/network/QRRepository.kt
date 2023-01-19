package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QRRepository @Inject constructor(
    private val qrService: QRService,
    private val transactionService: TransactionService,
    private val merchantService: MerchantService
) {

    fun login(loginRequest: LoginRequest) = qrService.login(loginRequest)

    fun register(registerRequest: RegisterRequest) = qrService.register(registerRequest)

    fun generateQR(qrModelRequest: QrModelRequest) = qrService.generateQR(qrModelRequest)

    fun getCardSchemes() = qrService.getCardSchemes()

    fun getCardBanks() = qrService.getCardBanks()


    fun getAllTransaction(qrCodeId: String) = transactionService.getEachTransaction(qrCodeId, 1, 10)


   // fun getMerchant(searchQuery: String) = merchantService.getMerchant(searchQuery)

   // fun getAllMerchant() = merchantService.getAllMerchant()
}