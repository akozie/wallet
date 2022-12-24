package com.woleapp.netpos.qrgenerator.ui.model

data class QrModelRequest(
    val card_cvv: String,
    val card_expiry: String,
    val card_number: String,
    val card_scheme: String,
    val email: String,
    val fullname: String,
    val issuing_bank: String,
    val mobile_phone: String
)