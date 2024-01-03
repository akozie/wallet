package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.WalletLoginRequest
import com.woleapp.netpos.qrgenerator.model.referrals.ConfirmReferralModel
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyModel
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WalletRepository @Inject constructor(
    private val walletService: WalletService
) {

    fun fetchWallet(token: String) = walletService.fetchWallet(token)

    fun verifyWalletNumber(token: String, verifyWallet: Boolean) =
        walletService.verifyWalletNumber(token, verifyWallet)

    fun verifyWalletOTP(token: String, otp: String) =
        walletService.verifyWalletOTP(token, VerifyOTPRequest(otp))

    fun confirmTransactionPin(
        token: String,
        confirmTransactionPin: ConfirmTransactionPin
    ) = walletService.confirmTransactionPin(token, confirmTransactionPin)

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

//    fun fetchQrToken(token: String) =
//        walletService.fetchQrToken(token)

    fun storeQrToken(token: String, qrTokenRequest: QrTokenRequest) =
        walletService.storeQrToken(token, qrTokenRequest)

//    fun confirmTransactionPin(token: String, confirmTransactionPin: ConfirmTransactionPin) =
//        walletService.confirmTransactionPin(token, confirmTransactionPin)

    fun updateTransactionPin(
        token: String,
        oldPin: String,
        newPin: String,
        otp: String,
        securityAnswer: String,
        securityQuestion: String,
        adminAccessToken: String,
        userTokenId: String,
        accountId: String
    ) =
        walletService.updateTransactionPin(
            token,
            UpdateTransactionPinRequest(oldPin, newPin, otp, securityAnswer, securityQuestion, adminAccessToken, userTokenId, accountId)
        )

    fun sendEmailReceipt(
        token: String,
        transactionReceiptRequest: TransactionReceiptRequest
    ) =
        walletService.sendEmailReceipt(
            token,
            transactionReceiptRequest
        )

    fun getSelectedQuestion(token: String) =
        walletService.getSelectedQuestion(token)

    fun fetchOtherAccount(token: String, accountNumber: FindAccountNumberRequest) =
        walletService.fetchOtherAccount(token, accountNumber)

    fun fetchProvidusAccount(token: String, accountNumber: FindAccountNumberRequest) =
        walletService.fetchProvidusAccount(token, accountNumber)

    fun providusToProvidus(token: String, providusRequest: ProvidusRequest) =
        walletService.providusToProvidus(token, providusRequest)

    fun providusToOtherBanks(token: String, otherBanksRequest: OtherBanksRequest) =
        walletService.providusToOtherBanks(token, otherBanksRequest)

    fun getWalletStatus(token: String) =
        walletService.getWalletStatus(token)

    fun inviteToTally(token: String, inviteToTallyModel: InviteToTallyModel) =
        walletService.inviteToTally(token, inviteToTallyModel)

    fun confirmReferral(confirmReferralModel: ConfirmReferralModel) =
        walletService.confirmReferral(confirmReferralModel)

    fun walletLogin(loginRequest: WalletLoginRequest) =
        walletService.walletLogin(loginRequest)

}