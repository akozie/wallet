package com.woleapp.netpos.qrgenerator.model.wallet.request

data class FindAccountNumberRequest(
    val accountNumber: String,
    val beneficiaryBank: String
)