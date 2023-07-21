package com.woleapp.netpos.qrgenerator.model.wallet

data class WithdrawalResponse(
    val Available_Balance: Int,
    val Pending_Balance: Int,
    val Successful: String,
    val Transaction_ref: String
)