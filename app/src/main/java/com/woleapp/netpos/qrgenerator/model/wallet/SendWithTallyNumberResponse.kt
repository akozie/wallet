package com.woleapp.netpos.qrgenerator.model.wallet

data class SendWithTallyNumberResponse(
    val Transaction_ref: String,
    val amount: String,
    val available_balance: Int,
    val message: String,
    val pending_balance: Int,
    val status: String
)
