package com.woleapp.netpos.qrgenerator.model.verve

data class SendOtpForVerveCardModel(
    val OTPData: String,
    val type: String = "OTP"
)
