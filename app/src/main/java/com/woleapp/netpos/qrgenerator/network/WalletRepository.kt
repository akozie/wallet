package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WalletRepository @Inject constructor(
    private val walletService: WalletService
) {

    fun fetchWallet(token: String) = walletService.fetchWallet(token)

    fun verifyWalletOTP(token: String, otp: String) =
        walletService.verifyWalletOTP(token, VerifyOTPRequest(otp))

    fun sendWithTallyNumber(
        token: String,
        sendWithTallyNumberRequest: SendWithTallyNumberRequest
    ) = walletService.sendWithTallyNumber(token, sendWithTallyNumberRequest)


    fun setTransactionPin(
        token: String,
        transactionPin: String,
        securityQuestionId: String,
        securityQuestion: String,
        securityAnswer: String
    ) = walletService.setTransactionPin(
        token,
        SetPINRequest(transactionPin, securityQuestionId, securityQuestion, securityAnswer)
    )

    fun getUserTransactions(token: String, recordsNumber: Int) =
        walletService.getUserTransactions(token, TallyWalletUserTransactionRequest(recordsNumber))

    fun creditWallet(token: String, transactionID: String) =
        walletService.creditWallet(token, CreditWalletRequest(transactionID))

    fun getSecurityQuestions(token: String) =
        walletService.getSecurityQuestions(token)

    fun getOtpVerificationToUpdatePin(token: String) =
        walletService.getOtpVerificationToUpdatePin(token)

    fun updateTransactionPin(
        token: String,
        newPin: String,
        otp: String,
        securityAnswer: String,
        securityQuestion: Int
    ) =
        walletService.updateTransactionPin(
            token,
            UpdateTransactionPinRequest(newPin, otp, securityAnswer, securityQuestion)
        )

    fun getSelectedQuestion(token: String) =
        walletService.getSelectedQuestion(token)

}