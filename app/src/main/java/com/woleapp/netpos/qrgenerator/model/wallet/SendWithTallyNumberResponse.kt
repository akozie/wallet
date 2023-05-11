package com.woleapp.netpos.qrgenerator.model.wallet

data class SendWithTallyNumberResponse(
    val status: String,
    val Transaction_ref: String,
    val message: String,
    val amount: String
)