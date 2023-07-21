package com.woleapp.netpos.qrgenerator.model.wallet.request

data class VerifyOTPRequest(
    val otp: String
)

data class ReInitializeOTP(
    val reintializeOTP: Boolean
)