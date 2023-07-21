package com.woleapp.netpos.qrgenerator.model.wallet

data class QRTokenResponseItem(
    val qr_code_id: String,
    val qr_token: String,
    val user_id: String
)