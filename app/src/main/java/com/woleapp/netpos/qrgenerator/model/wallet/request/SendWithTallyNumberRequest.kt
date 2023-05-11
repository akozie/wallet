package com.woleapp.netpos.qrgenerator.model.wallet.request

data class SendWithTallyNumberRequest(
    val dest_account: String,
    val transaction_amount: String,
    val transaction_pin: String
)