package com.woleapp.netpos.qrgenerator.model.wallet

data class CreditWalletCardResponse(
    val available_balance: Int,
    val message: String,
    val pending_balance: Int,
    val status: String
)