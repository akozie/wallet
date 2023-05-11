package com.woleapp.netpos.qrgenerator.model.wallet

data class WalletModelResponse(
    val balance: Int?,
    val message: String?,
    val phone_no: String?,
    val fullname: String?,
    val email: String?
)