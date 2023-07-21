package com.woleapp.netpos.qrgenerator.model.wallet

data class QRTokenResponse(
    val `data`: List<QRTokenResponseItem>,
    val status: String
)