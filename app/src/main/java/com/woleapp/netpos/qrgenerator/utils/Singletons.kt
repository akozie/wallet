package com.woleapp.netpos.qrgenerator.utils

import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.AmountAndTallyNumber
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.User
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutResponse
import com.woleapp.netpos.qrgenerator.model.wallet.WalletModelResponse
import com.woleapp.netpos.qrgenerator.model.wallet.WalletUserResponse

class Singletons {
    val gson = Gson()

    fun getCurrentlyLoggedInUser(): User? =
        gson.fromJson(Prefs.getString(PREF_USER, ""), User::class.java)

    fun getPin(): String? =
        Prefs.getString(PIN_PASSWORD, "")

    fun getAmountAndTallyNumber(): AmountAndTallyNumber? =
        gson.fromJson(Prefs.getString(AMOUNT_AND_TALLY_NUMBER, ""), AmountAndTallyNumber::class.java)

    fun getSavedQrModelRequest(): QrModelRequest? =
        gson.fromJson(Prefs.getString(PREF_GENERATE_QR, ""), QrModelRequest::class.java)

    fun getTallyWalletBalance(): WalletUserResponse? =
        gson.fromJson(Prefs.getString(PREF_TALLY_WALLET, ""), WalletUserResponse::class.java)

    fun getTransAmountAndId(): CheckOutResponse? =
        gson.fromJson(Prefs.getString(TRANS_ID_AND_AMOUNT, ""), CheckOutResponse::class.java)

    fun getTallyUserToken(): String? =
        Prefs.getString(USER_TOKEN, "")

    fun getLoginPassword(): String =
        Prefs.getString(LOGIN_PASSWORD, "")

}