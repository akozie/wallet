package com.woleapp.netpos.qrgenerator.network

import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.model.GeneralResponse
import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface WalletService {

    @GET("fetchWallet")
    fun verifyWalletNumber(
        @Header("Authorization") token: String,
        @Query("reintializeOTP") reInitializeOTP: Boolean
    ): Single<WalletModelResponse>

    @GET("fetchWallet")
    fun fetchWallet(
        @Header("Authorization") token: String
    ): Single<WalletModelResponse>

    @POST("verifyOTP")
    fun verifyWalletOTP(
        @Header("Authorization") token: String,
        @Body verifyOTPRequest: VerifyOTPRequest
    ): Single<OTPWalletResponse>

    @POST("peertopeer")
    fun sendWithTallyNumber(
        @Header("Authorization") token: String,
        @Body sendWithTallyNumberRequest: SendWithTallyNumberRequest
    ): Single<Response<SendWithTallyNumberResponse>>

    @POST("setTransactionPin")
    fun setTransactionPin(
        @Header("Authorization") token: String,
        @Body setPINRequest: SetPINRequest
    ): Single<GeneralWalletResponse>

    @HTTP(method = "POST", path = "userTransactions", hasBody = true)
    fun getUserTransactions(
        @Header("Authorization") token: String,
        @Body tallyWalletUserTransactionRequest: TallyWalletUserTransactionRequest
    ): Single<TallyWalletUserTransactionsResponse>

    @POST("creditWalletCard")
    fun creditWallet(
        @Header("Authorization") token: String,
        @Body creditWalletRequest: CreditWalletRequest
    ): Single<CreditWalletCardResponse>

    @GET("getSecurityQuestions")
    fun getSecurityQuestions(
        @Header("Authorization") token: String
    ): Single<List<GetSecurityQuestionResponseItem>>
    //check this function //ask Ezekiel what update pin endpoint is

    @POST("PinOtpVerification")
    fun getOtpVerificationToUpdatePin(
        @Header("Authorization") token: String
    ): Single<OtpVerificationToUpdatePinResponse>

    @POST("confirmTransactionPin")
    fun confirmTransactionPin(
        @Header("Authorization") token: String,
        @Body confirmTransactionPin: ConfirmTransactionPin
    ): Single<Response<ConfirmTransactionPinResponse>>

    @POST("updateTransactionPin")
    fun updateTransactionPin(
        @Header("Authorization") token: String,
        @Body updateTransactionPinRequest: UpdateTransactionPinRequest
    ): Single<GeneralWalletResponse>

    @POST("generateReceipt")
    fun getTransactionReceipt(
        @Header("Authorization") token: String,
        @Body transactionReceiptRequest: TransactionReceiptRequest
    ): Single<TransactionReceiptResponse>

    @POST("sendEmailReceipt")
    fun sendEmailReceipt(
        @Header("Authorization") token: String,
        @Body transactionReceiptRequest: TransactionReceiptRequest
    ): Single<TransactionReceiptResponse>

    @POST("storeQrToken")
    fun storeQrToken(
        @Header("Authorization") token: String,
        @Body qrTokenRequest: QrTokenRequest
    ): Single<GeneralResponse>

    @GET("fetchQrToken")
    fun fetchQrToken(
        @Header("Authorization") token: String
    ): Single<Response<FetchQrTokenResponse>>

    @DELETE("deleteQrToken")
    fun deleteQrToken(
        @Header("Authorization") token: String,
        @Body deleteQrTokenRequest: DeleteQrTokenRequest
    ): Single<GeneralResponse>

    @GET("userSecurityQuestion")
    fun getSelectedQuestion(
        @Header("Authorization") token: String
    ): Single<GetSelectedQuestionResponse>

        @GET("fetchNIPAccount")
    fun fetchOtherAccount(
        @Header("Authorization") token: String,
        @Body findAccountNumberRequest: FindAccountNumberRequest
    ): Single<FindAccountNumberResponse>

        @GET("fetchProvidusAccount")
    fun fetchProvidusAccount(
        @Header("Authorization") token: String,
        @Body findAccountNumberRequest: FindAccountNumberRequest
    ): Single<FindAccountNumberResponse>

        @POST("p2p")
    fun providusToProvidus(
        @Header("Authorization") token: String,
        @Body providusRequest: ProvidusRequest
    ): Single<WithdrawalResponse>

        @POST("p2e")
    fun providusToOtherBanks(
        @Header("Authorization") token: String,
        @Body otherBanksRequest: OtherBanksRequest
    ): Single<WithdrawalResponse>

        @GET("WalletStatus")
    fun getWalletStatus(
        @Header("Authorization") token: String
    ): Single<JsonObject>

}