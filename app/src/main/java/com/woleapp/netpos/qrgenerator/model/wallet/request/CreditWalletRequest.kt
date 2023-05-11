package com.woleapp.netpos.qrgenerator.model.wallet.request

data class CreditWalletRequest(
    val transaction_amount: String,
    val transaction_id: String
)