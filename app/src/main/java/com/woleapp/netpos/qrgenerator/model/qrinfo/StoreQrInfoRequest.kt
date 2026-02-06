package com.woleapp.netpos.qrgenerator.model.qrinfo

data class StoreQrInfoRequest(
    val card_scheme: String,
    val email: String,
    val issuing_bank: String,
    val qr_code_id: String
)