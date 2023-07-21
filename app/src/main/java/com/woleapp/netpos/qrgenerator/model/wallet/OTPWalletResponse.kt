package com.woleapp.netpos.qrgenerator.model.wallet

data class OTPWalletResponse(
    val message: String,
    val status: String,
    val tally_number: String,
    val verified: Int,
    val available_balance: Int,
    val pending_balance: Int,

)