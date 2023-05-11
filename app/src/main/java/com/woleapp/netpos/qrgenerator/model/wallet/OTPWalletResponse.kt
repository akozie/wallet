package com.woleapp.netpos.qrgenerator.model.wallet

data class OTPWalletResponse(
    val message: String,
    val status: String,
    val tally_no: String,
    val balance: String
)