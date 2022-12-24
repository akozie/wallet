package com.woleapp.netpos.qrgenerator.ui.network

import com.woleapp.netpos.qrgenerator.ui.model.LoginRequest
import com.woleapp.netpos.qrgenerator.ui.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.ui.model.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QRRepository @Inject constructor(
    private val qrService: QRService
) {

    fun login(loginRequest: LoginRequest) = qrService.login(loginRequest)

    fun register(registerRequest: RegisterRequest) = qrService.register(registerRequest)

    fun generateQR(qrModelRequest: QrModelRequest) = qrService.generateQR(qrModelRequest)


    fun getCardSchemes() = qrService.getCardSchemes()


    fun getCardBanks() = qrService.getCardBanks()


}