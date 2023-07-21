package com.woleapp.netpos.qrgenerator.model.wallet.request

data class QrTokenRequest(
    val qr_code_id: String,
    val qr_token: String,
    val card_scheme: String,
    val issuing_bank: String
)