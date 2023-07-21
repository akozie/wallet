package com.woleapp.netpos.qrgenerator.model.wallet.request

data class ProvidusRequest(
    val creditAccount: String,
    val creditAccountName: String,
    val narration: String,
    val transactionAmount: String
)