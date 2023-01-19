package com.woleapp.netpos.qrgenerator.model

data class TransactionResponse(
    val `data`: Transactionss,
    val message: String,
    val status: String
)