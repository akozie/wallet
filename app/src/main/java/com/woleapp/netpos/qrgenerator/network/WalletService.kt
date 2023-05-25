package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import io.reactivex.Single
import retrofit2.http.*

interface WalletService {

    @GET("transactions/fetchWallet")
    fun fetchWallet(
        @Header("Authorization") token: String
    ): Single<WalletModelResponse>

    @POST("transactions/verifyOTP")
    fun verifyWalletOTP(
        @Header("Authorization") token: String,
        @Body verifyOTPRequest: VerifyOTPRequest
    ): Single<OTPWalletResponse>

    @POST("transactions/peertopeer")
    fun sendWithTallyNumber(
        @Header("Authorization") token: String,
        @Body sendWithTallyNumberRequest: SendWithTallyNumberRequest
    ): Single<SendWithTallyNumberResponse>

    @POST("transactions/setTransactionPin")
    fun setTransactionPin(
        @Header("Authorization") token: String,
        @Body setPINRequest: SetPINRequest
    ): Single<GeneralWalletResponse>

    @HTTP(method = "POST", path = "transactions/userTransactions", hasBody = true)
    fun getUserTransactions(
        @Header("Authorization") token: String,
        @Body tallyWalletUserTransactionRequest: TallyWalletUserTransactionRequest
    ): Single<TallyWalletUserTransactionsResponse>

    @POST("transactions/creditWalletCard")
    fun creditWallet(
        @Header("Authorization") token: String,
        @Body creditWalletRequest: CreditWalletRequest
    ): Single<GeneralWalletResponse>

    @GET("transactions/getSecurityQuestions")
    fun getSecurityQuestions(
        @Header("Authorization") token: String
    ): Single<List<GetSecurityQuestionResponseItem>>

    @POST("transactions/PinOtpVerification")
    fun getOtpVerificationToUpdatePin(
        @Header("Authorization") token: String
    ): Single<OtpVerificationToUpdatePinResponse>

    @POST("transactions/updateTransactionPin")
    fun updateTransactionPin(
        @Header("Authorization") token: String,
        @Body updateTransactionPinRequest: UpdateTransactionPinRequest
    ): Single<GeneralWalletResponse>

}