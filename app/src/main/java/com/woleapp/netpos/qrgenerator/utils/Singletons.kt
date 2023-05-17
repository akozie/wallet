package com.woleapp.netpos.qrgenerator.utils

import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.AmountAndTallyNumber
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.User
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutResponse
import com.woleapp.netpos.qrgenerator.model.wallet.WalletModelResponse

class Singletons {
    val gson = Gson()

    fun getCurrentlyLoggedInUser(): User? =
        gson.fromJson(Prefs.getString(PREF_USER, ""), User::class.java)

    fun getAmountAndTallyNumber(): AmountAndTallyNumber? =
        gson.fromJson(Prefs.getString(AMOUNT_AND_TALLY_NUMBER, ""), AmountAndTallyNumber::class.java)

    fun getSavedQrModelRequest(): QrModelRequest? =
        gson.fromJson(Prefs.getString(PREF_GENERATE_QR, ""), QrModelRequest::class.java)

    fun getTallyWalletBalance(): WalletModelResponse? =
        gson.fromJson(Prefs.getString(PREF_TALLY_WALLET, ""), WalletModelResponse::class.java)

    fun getTransAmountAndId(): CheckOutResponse? =
        gson.fromJson(Prefs.getString(TRANS_ID_AND_AMOUNT, ""), CheckOutResponse::class.java)

    fun getTallyUserToken(): String? =
        Prefs.getString(USER_TOKEN, "")

}