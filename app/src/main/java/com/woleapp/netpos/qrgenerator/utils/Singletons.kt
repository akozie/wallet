package com.woleapp.netpos.qrgenerator.utils

import android.content.Context
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

    fun getCurrentlyLoggedInUser(context: Context): User? =
        gson.fromJson(EncryptedPrefsUtils.getString(context, PREF_USER), User::class.java)

    fun getPin(): String? =
        Prefs.getString(PIN_PASSWORD, "")

    fun getAmountAndTallyNumber(context: Context): AmountAndTallyNumber? =
        gson.fromJson(EncryptedPrefsUtils.getString(context, AMOUNT_AND_TALLY_NUMBER), AmountAndTallyNumber::class.java)

    fun getSavedQrModelRequest(context: Context): QrModelRequest? =
        gson.fromJson(EncryptedPrefsUtils.getString(context, PREF_GENERATE_QR), QrModelRequest::class.java)

    fun getTallyWalletBalance(context: Context): WalletUserResponse? =
        gson.fromJson(EncryptedPrefsUtils.getString(context, PREF_TALLY_WALLET), WalletUserResponse::class.java)

    fun getTallyWalletBalanceTest(context: Context): String? =
        EncryptedPrefsUtils.getString(context, PREF_TALLY_WALLET_TEST)

    fun getTransAmountAndId(context: Context): CheckOutResponse? =
        gson.fromJson(EncryptedPrefsUtils.getString(context, TRANS_ID_AND_AMOUNT), CheckOutResponse::class.java)

    fun getTallyUserToken(context: Context): String? =
        EncryptedPrefsUtils.getString(context, USER_TOKEN)

    fun getAdminAccessToken(context: Context): String? =
        EncryptedPrefsUtils.getString(context, ADMIN_ACCESS_TOKEN)

    fun getAccountId(context: Context): String? =
        EncryptedPrefsUtils.getString(context, ACCOUNT_ID)

    fun getWalletUserTokenId(context: Context): String? =
        EncryptedPrefsUtils.getString(context, USER_TOKEN_ID)

    fun getLoginPassword(context: Context): String? =
        EncryptedPrefsUtils.getString(context, LOGIN_PASSWORD)

    fun getLoginPasswordValue(context: Context): String? =
        EncryptedPrefsUtils.getString(context, LOGIN_PASSWORD_VALUE)

    fun getRefreshToken(context: Context): String? =
        EncryptedPrefsUtils.getString(context, REFRESH_TOKEN)

}