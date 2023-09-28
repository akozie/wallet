package com.woleapp.netpos.qrgenerator.utils

object UtilityParam {

    init {
        System.loadLibrary("api-keys")
    }

    private external fun getTallyBaseUrl(): String
    private external fun getTransactionBaseUrl():String
    private external fun getMerchantBaseUrl(): String
    private external fun getCheckoutBaseUrl(): String
    private external fun getAuthUserName(): String
    private external fun getAuthPassword(): String
    private external fun getCheckoutMerchantId(): String
    private external fun getWebViewBaseUrl(): String
    private external fun getTallyWalletBaseUrl(): String
    private external fun getWalletXAPIToken(): String
    private external fun getBearerToken(): String

    private external fun getTallyConstant(): String

    private external fun getMerchantHeaderToken(): String

     val STRING_TALLY_BASE_URL= getTallyBaseUrl()
     val STRING_TRANSACTION_BASE_URL= getTransactionBaseUrl()
     val STRING_MERCHANT_BASE_URL= getMerchantBaseUrl()
     val STRING_CHECKOUT_BASE_URL= getCheckoutBaseUrl()
     val STRING_AUTH_USER_NAME= getAuthUserName()
     val STRING_AUTH_PASSWORD= getAuthPassword()
     val STRING_CHECKOUT_MERCHANT_ID= getCheckoutMerchantId()
     val STRING_WEB_VIEW_BASE_URL= getWebViewBaseUrl()
    val STRING_TALLY_WALLET_BASE_URL= getTallyWalletBaseUrl()
    val STRING_WALLET_X_API_TOKEN = getWalletXAPIToken()
    val STRING_TALLY_CONSTANT = getTallyConstant()
    val STRING_MERCHANT_HEADER_TOKEN = getMerchantHeaderToken()


    val STRING_WALLET_BEARER_TOKEN = getBearerToken()
}