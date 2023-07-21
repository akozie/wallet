package com.woleapp.netpos.qrgenerator.model.wallet.request

data class FindAccountNumberResponse(
    val accountName: String,
    val accountNumber: String,
    val status: String
)