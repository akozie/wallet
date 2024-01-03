package com.woleapp.netpos.qrgenerator.model

data class GeneralResponse(
    val success : Boolean,
    val message: String
)

data class ConfirmReferralResponse(
    val status : String,
    val message: String
)