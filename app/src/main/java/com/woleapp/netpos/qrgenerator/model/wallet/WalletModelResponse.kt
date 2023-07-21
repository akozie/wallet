package com.woleapp.netpos.qrgenerator.model.wallet

data class WalletModelResponse(
    val available_balance: Int?,
    val pending_balance: Int?,
    val message: String?,
    val phone_no: String?,
    val fullname: String?,
    val verified: Int,
    val email: String?,
    val pin: Boolean
)
